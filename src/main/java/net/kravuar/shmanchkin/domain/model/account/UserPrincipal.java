package net.kravuar.shmanchkin.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private UUID uuid;
    private String username;
}