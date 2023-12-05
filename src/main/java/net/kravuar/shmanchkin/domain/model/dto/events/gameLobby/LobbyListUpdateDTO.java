package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.FullLobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;

@Getter
@Setter
public class LobbyListUpdateDTO extends EventDTO {
    private FullLobbyDTO game;

    public LobbyListUpdateDTO(GameLobby gameLobby, LobbyListUpdateAction action) {
        super(action == LobbyListUpdateAction.CREATED
                ? "game-created"
                : "game-closed"
        );
        this.game = new FullLobbyDTO(gameLobby);
    }
}
