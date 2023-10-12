package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.Player;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

import java.util.Collection;

@Data
public class UserInfoDTO {
    private String currentLobbyName;
    private String username;

    public UserInfoDTO(UserInfo userInfo) {
        this.currentLobbyName = userInfo.getUsername();
        this.username = userInfo.getUsername();
    }
}
