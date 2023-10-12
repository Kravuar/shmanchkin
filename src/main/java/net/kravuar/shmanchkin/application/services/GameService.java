package net.kravuar.shmanchkin.application.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.events.LobbyPlayerUpdate;
import net.kravuar.shmanchkin.domain.model.exceptions.AlreadyInGameException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameAlreadyExistsException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameNotFoundException;
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
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class GameService {
    private final ApplicationEventPublisher publisher;
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    @Getter // TODO: getter for test purposes, remove
    private final UserInfo currentUser;

    public Collection<Game> getGameList() {
        return Collections.unmodifiableCollection(games.values());
    }

    public Flux<ServerSentEvent<GameEvent>> createGame(GameFormDTO gameForm) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getGame().getLobbyName());
        var game = games.putIfAbsent(
                gameForm.getLobbyName(),
                new Game(
                        gameForm.getLobbyName(),
                        gameForm.getOwnerName(),
                        gameForm.getMaxPlayers()
                )
        );
        if (game != null)
            throw new GameAlreadyExistsException(game.getLobbyName());

        return joinGame(gameForm.getLobbyName(), gameForm.getOwnerName()); // I believe it won't throw anything as long as it's synchronized
    }

    public synchronized Flux<ServerSentEvent<GameEvent>> joinGame(String lobbyName, String username) {
        if (!currentUser.isIdle())
            throw new AlreadyInGameException(currentUser.getGame().getLobbyName());

        var game = games.get(lobbyName);
        if (game == null)
            throw new GameNotFoundException(lobbyName);

        currentUser.setGame(game);
        currentUser.setUsername(username);

//        TODO: this method gets stuck in some loop, resulting in usernamealreadytaken
//        Probably could also store currentUser for the stream via .contextWrite
        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (GameEvent) message.getPayload();
                var event = ServerSentEvent.<GameEvent> builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            sink.onCancel(() -> {
                game.unsubscribe(handler, currentUser);
                currentUser.toIdle();
                publisher.publishEvent(new LobbyPlayerUpdate(lobbyName, currentUser, "lobby-players-update", LobbyPlayerUpdate.LobbyPlayerAction.LEFT));
            });
            try {
                game.subscribe(handler, currentUser);
            } catch (Exception joinFailedException) {
//                Means someone else joined within return of the flux and actual subscription
                sink.error(joinFailedException);
                sink.complete();
            }
            publisher.publishEvent(new LobbyPlayerUpdate(lobbyName, currentUser, "lobby-players-update", LobbyPlayerUpdate.LobbyPlayerAction.JOINED));
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public boolean closeGame() {
        var closedGame = games.remove(currentUser.getGame().getLobbyName());
        currentUser.toIdle();
        return closedGame != null;
    }

    @EventListener(GameEvent.class)
    public void gameEventListener(GameEvent event) {
        var game = games.get(event.getLobbyName());
        game.publishEvent(new GenericMessage<>(event));
    }
}
