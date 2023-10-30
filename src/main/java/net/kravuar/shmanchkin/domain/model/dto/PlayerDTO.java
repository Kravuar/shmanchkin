package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;

@Data
public class PlayerDTO {
    private final String username;

    public PlayerDTO(UserInfo userInfo) {
        this.username = userInfo.getUsername();
    }
}
