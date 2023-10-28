package net.kravuar.shmanchkin.domain.model.game;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.FluxSink;

@Getter
@EqualsAndHashCode(of = {"game", "username"})
public class Player {
    @Getter
    private Game game;
    private FluxSink<ServerSentEvent<EventDTO>> sink;
    private MessageHandler messageHandler;

    private String username;
    private Character character; // TODO: Character field with stuff like level, hand, armor and so on.

    public Player(String username) {
        this.username = username;
    }

    public void setInfo(Game game, FluxSink<ServerSentEvent<EventDTO>> sink, MessageHandler messageHandler) {
        this.game = game;
        this.sink = sink;
        this.messageHandler = messageHandler;
    }

    public boolean isIdle() {
        return game == null || sink == null || messageHandler == null;
    }

    public void toIdle() {
        this.game = null;
        this.sink.complete();
        this.sink = null;
        this.messageHandler = null;
    }

    public void send(EventDTO eventMessage) {
        if (isIdle())
            throw new UserIsIdleException();
        messageHandler.handleMessage(new GenericMessage<>(eventMessage));
    }

    public void subscribe(SubscribableChannel channel) {
        if (isIdle())
            throw new UserIsIdleException();
        channel.subscribe(messageHandler);
    }

    public void unsubscribe(SubscribableChannel channel) {
        if (isIdle())
            throw new UserIsIdleException();
        channel.unsubscribe(messageHandler);
    }
}
