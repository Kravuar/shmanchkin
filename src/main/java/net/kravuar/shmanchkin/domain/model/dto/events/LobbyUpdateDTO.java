package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.UserInfoDTO;
import net.kravuar.shmanchkin.domain.model.events.LobbyPlayerUpdate;

@Getter
@Setter
public class LobbyUpdateDTO extends GameEventDTO {
    private UserInfoDTO userInfo;
    private LobbyPlayerUpdate.LobbyPlayerAction action;

    public LobbyUpdateDTO(LobbyPlayerUpdate update) {
        super("lobby-update");
        this.userInfo = new UserInfoDTO(update.getUserInfo());
        this.action = update.getAction();
    }
}
