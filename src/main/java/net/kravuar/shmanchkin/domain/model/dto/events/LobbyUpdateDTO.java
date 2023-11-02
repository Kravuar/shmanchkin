package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;
import net.kravuar.shmanchkin.domain.model.game.LobbyPlayerUpdateAction;

@Getter
@Setter
public class LobbyUpdateDTO extends EventDTO {
    private UserDTO player;

    public LobbyUpdateDTO(UserInfo userInfo, LobbyPlayerUpdateAction action) {
        super(action == LobbyPlayerUpdateAction.CONNECTED
                ? "player-connected"
                : "player-disconnected"
        );
        this.player = new UserDTO(userInfo);
    }
}
