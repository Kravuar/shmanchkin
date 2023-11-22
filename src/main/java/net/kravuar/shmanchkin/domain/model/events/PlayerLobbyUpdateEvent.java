package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import net.kravuar.shmanchkin.domain.model.game.LobbyPlayerUpdateAction;

@RequiredArgsConstructor
@Getter
public class PlayerLobbyUpdateEvent {
    private final GameLobby gameLobby;
    private final UserInfo player;
    private final LobbyPlayerUpdateAction action;
}
