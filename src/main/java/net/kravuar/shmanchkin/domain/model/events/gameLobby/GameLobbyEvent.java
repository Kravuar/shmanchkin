package net.kravuar.shmanchkin.domain.model.events.gameLobby;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@RequiredArgsConstructor
@Getter
public abstract class GameLobbyEvent {
    private final GameLobby gameLobby;
}
