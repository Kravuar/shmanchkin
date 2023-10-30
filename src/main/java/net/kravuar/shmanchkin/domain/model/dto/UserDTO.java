package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.account.UserPrincipal;

import java.util.UUID;

@Data
public class UserDTO {
    private final UUID uuid;
    private final String username;

    public UserDTO(UserPrincipal userPrincipal) {
        this.uuid = userPrincipal.getUuid();
        this.username = userPrincipal.getUsername();
    }
}
