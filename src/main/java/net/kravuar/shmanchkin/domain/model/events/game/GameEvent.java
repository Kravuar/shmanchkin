package net.kravuar.shmanchkin.domain.model.events.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.Game;

@RequiredArgsConstructor
@Getter
public abstract class GameEvent {
    private final Game game;
}
