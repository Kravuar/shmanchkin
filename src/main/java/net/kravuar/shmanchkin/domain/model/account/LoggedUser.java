package net.kravuar.shmanchkin.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class LoggedUser {
    @NonNull
    private String accessToken;
    @NonNull
    private String refreshToken;
    @NonNull
    private UserPrincipal userInfo;
}
