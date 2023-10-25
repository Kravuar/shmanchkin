package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

import java.util.Collection;

@Data
public class GameDTO {
    private String lobbyName;
    private String ownerName;
    private int maxPlayers;
    private Collection<PlayerDTO> playersJoined;

    public GameDTO(Game game) {
        this.lobbyName = game.getLobbyName();
        this.ownerName = game.getOwner().getUsername();
        this.maxPlayers = game.getMaxPlayers();
        this.playersJoined = game.getPlayers().stream()
                .map(UserInfo::getPlayer)
                .map(PlayerDTO::new)
                .toList();
    }
}
