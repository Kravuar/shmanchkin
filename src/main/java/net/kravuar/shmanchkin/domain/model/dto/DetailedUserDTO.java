package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;

@Getter
public class DetailedUserDTO extends UserDTO {
    private final FullLobbyDTO currentGame;

    public DetailedUserDTO(UserInfo userInfo) {
        super(userInfo);
        if (!userInfo.isIdle())
            this.currentGame = new FullLobbyDTO(userInfo.getSubscription().getGameLobby());
        else
            this.currentGame = null;
    }
}
