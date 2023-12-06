package net.kravuar.shmanchkin.domain.model.exceptions.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class IllegalGameStageException extends GameException {
    private final Game.Stage stage;

    public IllegalGameStageException(Game.Stage stage) {
        super("Невозможно выполнить действие в данном этапе игры: " + stage + ".");
        this.stage = stage;
    }
}
