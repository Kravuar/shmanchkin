package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

@Data
public class UserInfoDTO {
    private String username;

    public UserInfoDTO(UserInfo userInfo) {
        this.username = userInfo.getUsername();
    }
}
