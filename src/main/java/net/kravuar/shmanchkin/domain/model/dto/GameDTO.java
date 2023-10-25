package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Game;

import java.util.Collection;

@Data
public class GameDTO {
    private String lobbyName;
    private UserInfoDTO owner;
    private int maxPlayers;
    private Collection<UserInfoDTO> playersJoined;

    public GameDTO(Game game) {
        this.lobbyName = game.getLobbyName();
        this.owner = new UserInfoDTO(game.getOwner());
        this.maxPlayers = game.getMaxPlayers();
        this.playersJoined = game.getPlayers().stream()
                .map(UserInfoDTO::new)
                .toList();
    }
}
