package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;

@Getter
public class DetailedUserDTO extends UserDTO {
    private final LobbyDTO currentGame;

    public DetailedUserDTO(UserInfo userInfo) {
        super(userInfo);
        var game = userInfo.getSubscription().getGameLobby();
        if (game != null)
            this.currentGame = new LobbyDTO(game);
        else
            this.currentGame = null;
    }
}
