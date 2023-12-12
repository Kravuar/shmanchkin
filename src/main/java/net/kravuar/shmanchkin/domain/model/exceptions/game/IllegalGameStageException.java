package net.kravuar.shmanchkin.domain.model.exceptions.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class IllegalGameStageException extends GameException {
    private final Game.TurnStage turnStage;

    public IllegalGameStageException(Game.TurnStage turnStage) {
        super("Невозможно выполнить действие в данном этапе игры: " + turnStage + ".");
        this.turnStage = turnStage;
    }
}
