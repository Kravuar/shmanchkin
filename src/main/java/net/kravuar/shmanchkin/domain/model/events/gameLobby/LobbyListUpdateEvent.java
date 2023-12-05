package net.kravuar.shmanchkin.domain.model.events.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;

@Getter
public class LobbyListUpdateEvent extends GameEvent {
    private final LobbyListUpdateAction action;

    public LobbyListUpdateEvent(GameLobby gameLobby, LobbyListUpdateAction action) {
        super(gameLobby);
        this.action = action;
    }
}
