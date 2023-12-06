package net.kravuar.shmanchkin.domain.model.events.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

import java.time.ZonedDateTime;

@Getter
public class MessageEvent extends GameLobbyEvent {
    private final String message;
    private final UserInfo sender;
    private final ZonedDateTime timestamp;

    public MessageEvent(GameLobby gameLobby, String message, UserInfo sender) {
        super(gameLobby);
        this.message = message;
        this.sender = sender;
        this.timestamp = ZonedDateTime.now();
    }
}
