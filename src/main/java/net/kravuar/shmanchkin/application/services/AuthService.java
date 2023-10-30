package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.JWTProps;
import net.kravuar.shmanchkin.domain.model.account.LoggedUser;
import net.kravuar.shmanchkin.domain.model.account.UserPrincipal;
import net.kravuar.shmanchkin.domain.model.dto.SignInFormDTO;
import net.kravuar.shmanchkin.domain.security.JWTExtractor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtils jwtUtils;
    private final JWTProps jwtProps;
    private final JWTExtractor jwtExtractor;

    public LoggedUser singIn(SignInFormDTO signInForm) {
        return getLoggedUser(
                new UserPrincipal(UUID.randomUUID(), signInForm.getUsername())
        );
    }

    public LoggedUser refresh(ServerHttpRequest request) {
        var decodedJWT = jwtUtils.decode(
                jwtExtractor.extract(request, jwtProps.getRefreshCookieName())
        );
        return getLoggedUser(new UserPrincipal(
                UUID.fromString(decodedJWT.getClaim("uuid").asString()),
                decodedJWT.getSubject()
        ));
    }

    private LoggedUser getLoggedUser(UserPrincipal userInfo) {
        var accessToken = jwtUtils.sign(
                jwtUtils.getJWTBuilder(
                        userInfo.getUsername(),
                        Collections.emptyList(),
                        jwtProps.getAccessTokenExpiration()
                ).withClaim("uuid", userInfo.getUuid().toString())
        );
        var refreshToken = jwtUtils.sign(
                jwtUtils.getJWTBuilder(
                        userInfo.getUsername(),
                        Collections.emptyList(),
                        jwtProps.getRefreshTokenExpiration()
                ).withClaim("uuid", userInfo.getUuid().toString())
        );

        return new LoggedUser(
                accessToken,
                refreshToken,
                userInfo
        );
    }
}
