package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;

@RequiredArgsConstructor
@Getter
public class LobbyListUpdateEvent {
    private final GameLobby gameLobby;
    private final LobbyListUpdateAction action;
}
