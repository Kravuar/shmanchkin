package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;

import java.util.Collection;

@Getter
@Setter
public class LobbyDTO extends GameDTO {
    private UserDTO owner;
    private Collection<UserDTO> playersJoined;

    public LobbyDTO(GameLobby gameLobby) {
        super(gameLobby);
        this.owner = new UserDTO(gameLobby.getOwner());
        this.playersJoined = gameLobby.getPlayers().values().stream()
                .map(UserDTO::new)
                .toList();
    }
}
