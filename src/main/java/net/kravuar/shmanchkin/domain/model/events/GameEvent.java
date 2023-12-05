package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@RequiredArgsConstructor
@Getter
public abstract class GameEvent {
    private final GameLobby gameLobby;
}
