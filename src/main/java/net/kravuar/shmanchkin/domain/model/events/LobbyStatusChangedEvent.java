package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import net.kravuar.shmanchkin.domain.model.game.LobbyStatus;

@RequiredArgsConstructor
@Getter
public class LobbyStatusChangedEvent {
    private final GameLobby gameLobby;
    private final LobbyStatus status;
}
