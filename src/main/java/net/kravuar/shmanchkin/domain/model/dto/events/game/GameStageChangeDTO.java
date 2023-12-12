package net.kravuar.shmanchkin.domain.model.dto.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameStageChangeDTO extends EventDTO {
    private final Game.TurnStage turnStage;

    public GameStageChangeDTO(Game.TurnStage turnStage) {
        super("game-stage-change");
        this.turnStage = turnStage;
    }
}
