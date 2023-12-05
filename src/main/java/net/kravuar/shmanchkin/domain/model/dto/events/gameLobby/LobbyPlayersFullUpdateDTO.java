package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;

import java.util.Collection;

@Getter
@Setter
public class LobbyPlayersFullUpdateDTO extends EventDTO {
    private Collection<UserDTO> players;

    public LobbyPlayersFullUpdateDTO(Collection<UserInfo> userInfos) {
        super("players-full-update");
        this.players = userInfos.stream().map(UserDTO::new).toList();
    }
}
