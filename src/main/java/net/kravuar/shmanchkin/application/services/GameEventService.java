package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.GameLobbyEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.SubscribableGameLobby;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameEventService implements MessageHandler {
    private final ApplicationEventPublisher publisher;

    public void addGameLobby(SubscribableGameLobby gameLobby) {
        gameLobby.subscribe(this);
    }

    public void removeGameLobby(SubscribableGameLobby gameLobby) {
        gameLobby.unsubscribe(this);
    }

    public void publishGameLobbyEvent(GameLobbyEvent gameLobbyEvent) {
        publisher.publishEvent(gameLobbyEvent);
    }

    @Override
    public void handleMessage(Message<?> gameEventMessage) throws MessagingException {
        var gameEvent = (GameLobbyEvent) gameEventMessage.getPayload();
        System.out.println(gameEvent);
        publisher.publishEvent(gameEvent);
    }
}
