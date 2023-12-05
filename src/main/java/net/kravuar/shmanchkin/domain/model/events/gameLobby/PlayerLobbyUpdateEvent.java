package net.kravuar.shmanchkin.domain.model.events.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction;

@Getter
public class PlayerLobbyUpdateEvent extends GameEvent {
    private final UserInfo player;
    private final LobbyPlayerUpdateAction action;

    public PlayerLobbyUpdateEvent(GameLobby gameLobby, UserInfo player, LobbyPlayerUpdateAction action) {
        super(gameLobby);
        this.player = player;
        this.action = action;
    }
}
