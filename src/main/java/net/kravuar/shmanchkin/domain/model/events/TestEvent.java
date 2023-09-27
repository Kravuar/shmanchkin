package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;

@Getter
public class TestEvent extends PlayerEvent {
    private final String message;

    public TestEvent(String lobbyName, String username, String eventType, String message) {
        super(lobbyName, username, eventType);
        this.message = message;
    }
}
