package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;

import java.util.Collection;

@Getter
@Setter
public class LobbyFullUpdateDTO extends EventDTO {
    private Collection<UserDTO> players;

    public LobbyFullUpdateDTO(Collection<UserInfo> userInfos) {
        super("players-full-update");
        this.players = userInfos.stream().map(UserDTO::new).toList();
    }
}
