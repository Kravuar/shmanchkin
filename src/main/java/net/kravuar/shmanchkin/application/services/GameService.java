package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameEventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.LobbyUpdateDTO;
import net.kravuar.shmanchkin.domain.model.events.LobbyPlayerUpdate;
import net.kravuar.shmanchkin.domain.model.exceptions.AlreadyInGameException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameAlreadyExistsException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameNotFoundException;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.MessageHandler;
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
    private final ApplicationEventPublisher publisher;
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final UserInfo currentUser;

    public Collection<GameDTO> getGameList() {
        return games.values().stream()
                .map(GameDTO::new).toList();
    }

    public void createGame(GameFormDTO gameForm) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getGame().getLobbyName());
        var game = games.putIfAbsent(
                gameForm.getLobbyName(),
                new Game(
                        gameForm.getLobbyName(),
                        currentUser,
                        gameForm.getMaxPlayers()
                )
        );
        if (game != null)
            throw new GameAlreadyExistsException(game.getLobbyName());
    }

    public synchronized Flux<ServerSentEvent<GameEventDTO>> joinGame(String lobbyName, String username) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getGame().getLobbyName());

        var game = games.get(lobbyName);
        if (game == null)
            throw new GameNotFoundException(lobbyName);

        currentUser.setGame(game);
        currentUser.setUsername(username);

//        Probably could also store currentUser for the stream via .contextWrite
        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (GameEventDTO) message.getPayload();
                var event = ServerSentEvent.<GameEventDTO>builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            sink.onCancel(() -> {
                        currentUser.toIdle();
                        game.unsubscribe(handler, currentUser);
                        publisher.publishEvent(
                                new LobbyPlayerUpdate(
                                        game,
                                        currentUser,
                                        LobbyPlayerUpdate.LobbyPlayerAction.LEFT)
                        );
                    }
            );
            try {
                game.subscribe(handler, currentUser);
            } catch (Exception joinFailedException) {
//                Means someone else joined within return of the flux and actual subscription
                sink.error(joinFailedException);
                sink.complete();
            }
            publisher.publishEvent(
                    new LobbyPlayerUpdate(
                            game,
                            currentUser,
                            LobbyPlayerUpdate.LobbyPlayerAction.JOINED
                    )
            );
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public void startGame() {
        if (currentUser.isIdle())
            throw new UserIsIdleException();
        currentUser.getGame().start();
    }

    public boolean closeGame() {
        var closedGame = games.remove(currentUser.getGame().getLobbyName());
        currentUser.toIdle();
        return closedGame != null;
    }

    @EventListener(LobbyPlayerUpdate.class)
    public void lobbyUpdateHandler(LobbyPlayerUpdate event) {
        event.getGame().publishEvent(new GenericMessage<>(new LobbyUpdateDTO(event)));
    }
}
