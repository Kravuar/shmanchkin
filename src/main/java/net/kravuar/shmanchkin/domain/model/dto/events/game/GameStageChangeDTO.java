package net.kravuar.shmanchkin.domain.model.dto.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameStageChangeDTO extends EventDTO {
    private final Game.Stage stage;

    public GameStageChangeDTO(Game.Stage stage) {
        super("game-stage-change");
        this.stage = stage;
    }
}
