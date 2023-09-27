package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.events.LobbyPlayerUpdate;
import net.kravuar.shmanchkin.domain.model.events.PlayerEvent;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.GameNotFoundException;
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

    public Collection<Game> getGameList() {
        return Collections.unmodifiableCollection(games.values());
    }

    public void createGame(GameFormDTO gameForm) {
        games.put(
                gameForm.getLobbyName(),
                new Game (
                        gameForm.getLobbyName(),
                        gameForm.getOwnerName(),
                        gameForm.getMaxPlayers()
                )
        );
    }

    public boolean close(String lobbyName) {
        return games.remove(lobbyName) != null;
    }

    public Flux<ServerSentEvent<GameEvent>> subscribe(String lobbyName, String username) {
        var game = games.get(lobbyName);
        if (game == null)
            throw new GameNotFoundException(lobbyName);
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
                game.unsubscribe(handler, username);
                publisher.publishEvent(new LobbyPlayerUpdate(lobbyName, username, "lobby-players-update", LobbyPlayerUpdate.LobbyPlayerAction.LEFT));
            });
            game.subscribe(handler, username);
            publisher.publishEvent(new LobbyPlayerUpdate(lobbyName, username, "lobby-players-update", LobbyPlayerUpdate.LobbyPlayerAction.JOINED));
        }, FluxSink.OverflowStrategy.LATEST);
    }

    @EventListener(GameEvent.class)
    public void gameEventListener(GameEvent event) {
        var game = games.get(event.getLobbyName());
        game.publishEvent(new GenericMessage<>(event));
    }
}
