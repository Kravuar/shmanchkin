package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import net.kravuar.shmanchkin.domain.model.game.LobbyListUpdateAction;

@RequiredArgsConstructor
@Getter
public class LobbyListUpdateEvent {
    private final GameLobby gameLobby;
    private final LobbyListUpdateAction action;
}
