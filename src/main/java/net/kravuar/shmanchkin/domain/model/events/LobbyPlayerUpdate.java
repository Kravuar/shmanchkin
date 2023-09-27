package net.kravuar.shmanchkin.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LobbyPlayerUpdate extends PlayerEvent {

    public enum LobbyPlayerAction {
        JOINED,
        LEFT
    }

    private final LobbyPlayerAction action;

    public LobbyPlayerUpdate(String lobbyName, String username, String eventType, LobbyPlayerAction action) {
        super(lobbyName, username, eventType);
        this.action = action;
    }
}
