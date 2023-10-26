package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.DetailedGameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.*;
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.game.*;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class GameService {
    private final UserInfo currentUser;
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final SubscribableChannel gameListChannel = MessageChannels.publishSubscribe().getObject();

    public Collection<DetailedGameDTO> getGameList() {
        return games.values().stream()
                .map(DetailedGameDTO::new).toList();
    }

    public Flux<ServerSentEvent<GameListUpdateDTO>> subscribeToGameListUpdates() {
        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (GameListUpdateDTO) message.getPayload();
                var event = ServerSentEvent.<GameListUpdateDTO>builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            gameListChannel.subscribe(handler);
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public void createGame(GameFormDTO gameForm) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getPlayer().getGame().getLobbyName());
        var game = new Game(
                gameForm.getLobbyName(),
                gameForm.getOwnerName()
        );
        var previousGame = games.putIfAbsent(
                gameForm.getLobbyName(),
                game
        );
        if (previousGame != null)
            throw new GameAlreadyExistsException(previousGame.getLobbyName());
        currentUser.setPlayer(game.getOwner());
        handleGameListUpdate(game, GameListUpdateAction.CREATED);
    }

    public synchronized Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName, String username) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getPlayer().getGame().getLobbyName());

        var game = games.get(lobbyName);
        if (game == null)
            throw new GameNotFoundException(lobbyName);

        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (EventDTO) message.getPayload();
                var event = ServerSentEvent.<EventDTO>builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            sink.onCancel(() -> {
                        game.removePlayer(currentUser.getPlayer());
                        handleLobbyPlayerUpdate(
                                game,
                                currentUser.getPlayer(),
                                LobbyPlayerUpdateAction.DISCONNECTED
                        );
                        currentUser.toIdle();
                    }
            );
            try {
                var player = new Player(username, game);
                currentUser.setPlayer(player);
                game.addPlayer(handler, player);
            } catch (Exception joinFailedException) {
//                Means someone else joined within return of the flux and actual subscription
                sink.error(joinFailedException);
                sink.complete();
            }
            handleLobbyPlayerUpdate(
                    game,
                    currentUser.getPlayer(),
                    LobbyPlayerUpdateAction.CONNECTED
            );
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public void kickPlayer(String username) {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        var game = currentUser.getPlayer().getGame();
        if (game.getOwner() != currentUser.getPlayer())
            throw new ForbiddenActionException(game.getLobbyName(), "Выгнать игрока");
        var player = game.getPlayers().get(username);
        if (player == null)
            throw new PlayerNotFoundException(game.getLobbyName(), username);
        handleLobbyPlayerUpdate(game, player, LobbyPlayerUpdateAction.KICKED);
        game.removePlayer(player);
    }

    public void startGame() {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        currentUser.getPlayer().getGame().start();
    }

    public void closeGame() {
        var game = games.remove(currentUser.getPlayer().getGame().getLobbyName());
        if (game != null) {
            handleGameListUpdate(game, GameListUpdateAction.CLOSED);
            currentUser.toIdle();
        }
    }

    private void handleLobbyPlayerUpdate(Game game, Player player, LobbyPlayerUpdateAction action) {
        game.send(new GenericMessage<>(new LobbyUpdateDTO(player, action)));
        game.send(new GenericMessage<>(new LobbyFullUpdateDTO(game.getPlayers().values())));
        if (action == LobbyPlayerUpdateAction.KICKED)
            game.sendTo(player, new GenericMessage<>(new KickedDTO()));
    }

    private void handleGameListUpdate(Game game, GameListUpdateAction action) {
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, action)));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
    }

    public void sendMessage(String message) {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        currentUser.getPlayer().getGame()
                .send(new GenericMessage<>(new MessageDTO(currentUser.getPlayer(), message)));
    }
}
