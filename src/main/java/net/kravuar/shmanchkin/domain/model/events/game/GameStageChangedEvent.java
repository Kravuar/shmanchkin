package net.kravuar.shmanchkin.domain.model.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameStageChangedEvent extends GameEvent {
    private final Game.TurnStage turnStage;

    public GameStageChangedEvent(Game game, Game.TurnStage turnStage) {
        super(game);
        this.turnStage = turnStage;
    }
}
