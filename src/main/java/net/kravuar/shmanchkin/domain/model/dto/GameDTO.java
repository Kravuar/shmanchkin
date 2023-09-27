package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.Player;

import java.util.List;

@Data
public class GameDTO {
    private String lobbyName;
    private String ownerName;
    private int maxPlayers;
//    TODO: PlayerDTO instead of username
    private List<String> playersJoined;

    public GameDTO(Game game) {
        this.lobbyName = game.getLobbyName();
        this.ownerName = game.getOwnerName();
        this.maxPlayers = game.getMaxPlayers();
        this.playersJoined = game.getPlayersJoined().stream().map(Player::getUsername).toList();
    }
}
