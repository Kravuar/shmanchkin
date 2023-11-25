package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Data
public class GameDTO {
    private String lobbyName;

    public GameDTO(GameLobby gameLobby) {
        this.lobbyName = gameLobby.getLobbyName();
    }
}
