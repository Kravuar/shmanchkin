package net.kravuar.shmanchkin.domain.model.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EventDTO {
    @JsonIgnore
    private final String eventType;
}