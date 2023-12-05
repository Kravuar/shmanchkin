package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

import java.util.Collection;

@Getter
@Setter
public class FullLobbyDTO extends LobbyDTO {
    private UserDTO owner;
    private Collection<UserDTO> playersJoined;

    public FullLobbyDTO(GameLobby gameLobby) {
        super(gameLobby);
        this.owner = new UserDTO(gameLobby.getOwner());
        this.playersJoined = gameLobby.getPlayers().stream()
                .map(UserDTO::new)
                .toList();
    }
}
