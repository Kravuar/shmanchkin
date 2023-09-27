package net.kravuar.shmanchkin.domain.model.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class GameEvent {
    @JsonIgnore
    private final String lobbyName;
    private final String eventType;
}
