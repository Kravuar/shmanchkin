package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

@Getter
public class LobbyPlayerUpdate extends PlayerEvent {

    public enum LobbyPlayerAction {
        JOINED,
        LEFT
    }

    private final LobbyPlayerAction action;

    public LobbyPlayerUpdate(String lobbyName, UserInfo userInfo, String eventType, LobbyPlayerAction action) {
        super(lobbyName, userInfo, eventType);
        this.action = action;
    }
}
