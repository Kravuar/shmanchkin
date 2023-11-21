package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.LobbyStatus;

@Getter
public class LobbyStatusChangedDTO extends EventDTO {
    private LobbyStatus status;

    public LobbyStatusChangedDTO(LobbyStatus status) {
        super("game-status-change");
        this.status = status;
    }
}
