package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Data
public class GameDTO {
    private String lobbyName;

    public GameDTO(Game game) {
        this.lobbyName = game.getLobbyName();
    }
}
