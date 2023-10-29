package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.DetailedGameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListFullUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.MessageDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.GameListUpdateAction;
import net.kravuar.shmanchkin.domain.model.game.Player;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;
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
import java.util.Objects;
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

    public Flux<ServerSentEvent<EventDTO>> subscribeToGameListUpdates() {
        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (EventDTO) message.getPayload();
                var event = ServerSentEvent.<EventDTO>builder()
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
        var owner = new Player(gameForm.getOwnerName());
        var game = new Game(
                gameForm.getLobbyName(),
                owner
        );
        var previousGame = games.putIfAbsent(
                gameForm.getLobbyName(),
                game
        );
        if (previousGame != null)
            throw new GameAlreadyExistsException(previousGame.getLobbyName());
        owner.setInfo(
                game,
                null,
                null
        );
        currentUser.setPlayer(owner);
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, GameListUpdateAction.CREATED)));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
    }

    public synchronized Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName, String username) {
        var game = games.get(lobbyName);
        if (game == null)
            throw new GameNotFoundException(lobbyName);

        Player newPlayer;

        if (!currentUser.isIdle())
            if (Objects.equals(username, game.getOwner().getUsername()))
                newPlayer = game.getOwner();
            else
                throw new AlreadyInGameException(currentUser.getPlayer().getGame().getLobbyName());
        else
            newPlayer = new Player(username);


        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (EventDTO) message.getPayload();
                var event = ServerSentEvent.<EventDTO>builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            try {
                newPlayer.setInfo(game, sink, handler);
                game.addPlayer(newPlayer);
                currentUser.setPlayer(newPlayer);
            } catch (Exception joinFailedException) {
//                Means someone else joined within return of the flux and actual subscription
                sink.error(joinFailedException);
                sink.complete();
                newPlayer.toIdle();
            }
            sink.onDispose(() -> game.removePlayer(newPlayer));
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public void leaveGame() {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        var game = currentUser.getPlayer().getGame();
        game.removePlayer(currentUser.getPlayer());
    }

    public void kickPlayer(String username) {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        var game = currentUser.getPlayer().getGame();
        if (!Objects.equals(game.getOwner(), currentUser.getPlayer()))
            throw new ForbiddenActionException(game.getLobbyName(), "Выгнать игрока");
        var player = game.getPlayers().get(username);
        if (player == null)
            throw new PlayerNotFoundException(game.getLobbyName(), username);
        game.removePlayer(player);
    }

    public void startGame() {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        var game = currentUser.getPlayer().getGame();
        if (!Objects.equals(game.getOwner(), currentUser.getPlayer()))
            throw new ForbiddenActionException(game.getLobbyName(), "Начать игру");
        game.start();
    }

    public void closeGame() {
        if (currentUser.getPlayer() == null || currentUser.getPlayer().getGame() == null)
            throw new UserIsIdleException("Нет созданной игры.");
        var game = currentUser.getPlayer().getGame();
        if (!Objects.equals(game.getOwner(), currentUser.getPlayer()))
            throw new ForbiddenActionException(game.getLobbyName(), "Закрыть игру");
        game.close();
        games.remove(game.getLobbyName());
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, GameListUpdateAction.CLOSED)));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(games.values())));
    }

    public void sendMessage(String message) {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        System.out.println("MESSAGE: " + message);
        currentUser.getPlayer().getGame().send(new MessageDTO(currentUser.getPlayer(), message));
    }
}
