package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.GameStatus;

@Getter
public class GameStatusChangedDTO extends EventDTO {
    private GameStatus status;

    public GameStatusChangedDTO(GameStatus status) {
        super("game-status-change");
        this.status = status;
    }
}
