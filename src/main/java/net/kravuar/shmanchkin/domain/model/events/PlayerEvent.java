package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;

@Getter
public class PlayerEvent extends GameEvent {
    private final String username;

    public PlayerEvent(String lobbyName, String username, String eventType) {
        super(lobbyName, eventType);
        this.username = username;
    }
}
