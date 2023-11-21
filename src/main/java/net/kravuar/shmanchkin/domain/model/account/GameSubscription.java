package net.kravuar.shmanchkin.domain.model.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
public class GameSubscription implements MessageHandler {
    @Getter
    @NonNull
    private final GameLobby gameLobby;
    @NonNull
    private final FluxSink<ServerSentEvent<EventDTO>> sink;
    @NonNull
    private final MessageHandler messageHandler;

    public void toIdle() {
        this.sink.complete();
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        messageHandler.handleMessage(message);
    }
}
