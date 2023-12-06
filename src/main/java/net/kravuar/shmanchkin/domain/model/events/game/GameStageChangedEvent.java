package net.kravuar.shmanchkin.domain.model.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameStageChangedEvent extends GameEvent {
    private final Game.Stage stage;

    public GameStageChangedEvent(Game game, Game.Stage stage) {
        super(game);
        this.stage = stage;
    }
}
