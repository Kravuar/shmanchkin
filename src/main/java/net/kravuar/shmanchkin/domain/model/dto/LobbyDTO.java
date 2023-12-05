package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Data
public class LobbyDTO {
    private String lobbyName;

    public LobbyDTO(GameLobby gameLobby) {
        this.lobbyName = gameLobby.getLobbyName();
    }
}
