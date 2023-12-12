package net.kravuar.shmanchkin.domain.model.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.UserIsIdleException;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

// TODO: Don't like accessing lobby/game/character via subscrumptption
@Getter
public class UserInfo {
    private final UUID uuid;
    private final String username;
    @Setter
    private GameSubscription subscription;

    public UserInfo(@NonNull UUID uuid, @NonNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public boolean isIdle() {
        return subscription == null;
    }

    public void toIdle() {
        if (isIdle())
            return;
        subscription.toIdle();
        subscription = null;
    }

    public void send(EventDTO eventMessage) {
        if (isIdle())
            throw new UserIsIdleException();
        subscription.handleMessage(new GenericMessage<>(eventMessage));
    }
}
