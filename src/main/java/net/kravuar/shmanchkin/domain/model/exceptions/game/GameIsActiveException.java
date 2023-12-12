package net.kravuar.shmanchkin.domain.model.exceptions.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameIsActiveException extends GameException {
    private final Game game;

    public GameIsActiveException(Game game) {
        super("Невозможно выполнить действие, игра уже начата.");
        this.game = game;
    }
}
