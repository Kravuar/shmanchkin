package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.Player;

import java.util.Collection;

@Data
public class GameDTO {
    private String lobbyName;
    private String ownerName;
    private int maxPlayers;
//    TODO: PlayerDTO instead of player
    private Collection<Player> playersJoined;

    public GameDTO(Game game) {
        this.lobbyName = game.getLobbyName();
        this.ownerName = game.getOwnerName();
        this.maxPlayers = game.getMaxPlayers();
        this.playersJoined = game.getPlayers();
    }
}
