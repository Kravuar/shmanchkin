package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class LobbyStatusChangedDTO extends EventDTO {
    private final GameLobby.LobbyStatus status;

    public LobbyStatusChangedDTO(GameLobby.LobbyStatus status) {
        super("game-status-change");
        this.status = status;
    }
}
