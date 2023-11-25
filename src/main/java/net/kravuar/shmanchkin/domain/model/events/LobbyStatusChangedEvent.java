package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyStatus;

@RequiredArgsConstructor
@Getter
public class LobbyStatusChangedEvent {
    private final GameLobby gameLobby;
    private final LobbyStatus status;
}
