package net.kravuar.shmanchkin.domain.model.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import net.kravuar.shmanchkin.domain.model.game.Character;
import net.kravuar.shmanchkin.domain.model.game.Game;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.FluxSink;

import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"uuid", "game", "username"})
public class UserInfo {
    @Getter
    private final UUID uuid;
    @Getter
    private String username;

    @Getter
    private Game game;
    private FluxSink<ServerSentEvent<EventDTO>> sink;
    private MessageHandler messageHandler;

    private Character character; // TODO: Character field with stuff like level, hand, armor and so on.

    public UserInfo(@NonNull UUID uuid, @NonNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public void setInfo(@NonNull Game game, FluxSink<ServerSentEvent<EventDTO>> sink, MessageHandler messageHandler) {
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
