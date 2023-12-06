package net.kravuar.shmanchkin.domain.model.events.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class LobbyStatusChangedEvent extends GameLobbyEvent {
    private final GameLobby.LobbyStatus lobbyStatus;

    public LobbyStatusChangedEvent(GameLobby gameLobby, GameLobby.LobbyStatus lobbyStatus) {
        super(gameLobby);
        this.lobbyStatus = lobbyStatus;
    }
}
