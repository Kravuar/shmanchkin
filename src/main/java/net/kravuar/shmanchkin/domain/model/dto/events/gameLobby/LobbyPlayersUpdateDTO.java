package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction;

@Getter
@Setter
public class LobbyPlayersUpdateDTO extends EventDTO {
    private UserDTO player;

    public LobbyPlayersUpdateDTO(UserInfo userInfo, LobbyPlayerUpdateAction action) {
        super(switch(action) {
            case CONNECTED -> "player-connected";
            case DISCONNECTED -> "player-disconnected";
            case KICKED -> "player-kicked";
        });
        this.player = new UserDTO(userInfo);
    }
}
