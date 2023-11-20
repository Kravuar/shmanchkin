package net.kravuar.shmanchkin.domain.model.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import net.kravuar.shmanchkin.domain.model.game.Character;
import org.springframework.messaging.SubscribableChannel;

import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"uuid"})
public class UserInfo {
    @Getter
    private final UUID uuid;
    @Getter
    private String username;
    @Getter
    @Setter
    private GameSubscriptionData subscription;

    public UserInfo(@NonNull UUID uuid, @NonNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public boolean isIdle() {
        return subscription == null || subscription.isIdle();
    }

    public void toIdle(SubscribableChannel channel) {
        if (isIdle())
            return;
        subscription.toIdle(channel);
        subscription = null;
    }

    public Character getCharacter() {
        if (isIdle())
            throw new UserIsIdleException();
//        return from GameLobby.Game.characterList
        return null;
    }

    public void send(EventDTO eventMessage) {
        if (isIdle())
            throw new UserIsIdleException();
        subscription.send(eventMessage);
    }

    public void subscribe(SubscribableChannel channel) {
        if (isIdle())
            throw new UserIsIdleException();
        subscription.subscribe(channel);
    }

    public void unsubscribe(SubscribableChannel channel) {
        if (isIdle())
            throw new UserIsIdleException();
        subscription.unsubscribe(channel);
    }
}
