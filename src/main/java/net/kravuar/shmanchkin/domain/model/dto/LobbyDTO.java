package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.game.Game;

import java.util.Collection;

@Getter
@Setter
public class LobbyDTO extends GameDTO {
    private UserDTO owner;
    private Collection<UserDTO> playersJoined;

    public LobbyDTO(Game game) {
        super(game);
        this.owner = new UserDTO(game.getOwner());
        this.playersJoined = game.getPlayers().values().stream()
                .map(UserDTO::new)
                .toList();
    }
}
