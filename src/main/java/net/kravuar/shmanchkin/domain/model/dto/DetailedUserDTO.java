package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;

@Getter
public class DetailedUserDTO extends UserDTO {
    private final LobbyDTO currentGame;

    public DetailedUserDTO(UserInfo userInfo) {
        super(userInfo);
        if (!userInfo.isIdle())
            this.currentGame = new LobbyDTO(userInfo.getSubscription().getGameLobby());
        else
            this.currentGame = null;
    }
}
