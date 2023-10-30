package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.JWTProps;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final JWTProps jwtProps;

    public void setJWTCookies(ServerHttpResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from(jwtProps.getAccessCookieName(), jwtProps.getTokenPrefix() + accessToken)
                .httpOnly(true)
                .maxAge(jwtProps.getAccessTokenExpiration())
                .path(jwtProps.getAccessCookiePath())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(jwtProps.getRefreshCookieName(), jwtProps.getTokenPrefix() + refreshToken)
                .httpOnly(true)
                .maxAge(jwtProps.getRefreshTokenExpiration())
                .path(jwtProps.getRefreshCookiePath())
                .build();

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public void invalidateJWTCookies(ServerHttpResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(jwtProps.getAccessCookieName(), "deleted")
                .httpOnly(true)
                .maxAge(0)
                .path(jwtProps.getAccessCookiePath())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(jwtProps.getRefreshCookieName(), "deleted")
                .httpOnly(true)
                .maxAge(0)
                .path(jwtProps.getRefreshCookiePath())
                .build();

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}