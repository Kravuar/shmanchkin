package net.kravuar.shmanchkin.domain.model.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
public class GameSubscriptionData {
    @Getter
    @NonNull
    private final GameLobby gameLobby;
    private final FluxSink<ServerSentEvent<EventDTO>> sink;
    private final MessageHandler messageHandler;

    public boolean isIdle() {
        return this.sink == null;
    }

    public void toIdle(SubscribableChannel channel) {

        this.sink.complete();
        unsubscribe(channel);
//        TODO: unsubscribe handler
    }

    public void send(EventDTO eventMessage) {
        assert messageHandler != null;
        messageHandler.handleMessage(new GenericMessage<>(eventMessage));
    }

    public void subscribe(SubscribableChannel channel) {
        channel.subscribe(messageHandler);
    }

    public void unsubscribe(SubscribableChannel channel) {
        channel.unsubscribe(messageHandler);
    }
}
