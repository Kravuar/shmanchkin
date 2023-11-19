package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;

@Data
public class GameDTO {
    private String lobbyName;

    public GameDTO(GameLobby gameLobby) {
        this.lobbyName = gameLobby.getLobbyName();
    }
}
