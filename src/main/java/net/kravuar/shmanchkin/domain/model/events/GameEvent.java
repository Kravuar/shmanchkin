package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
@RequiredArgsConstructor
public abstract class GameEvent {
    private final Game game;
}

