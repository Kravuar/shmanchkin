package net.kravuar.shmanchkin.application.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("web.jwt")
@Getter
@Setter
public class JWTProps {
    private String tokenPrefix = "Bearer_";
    private long accessTokenExpiration = 4000;
    private long refreshTokenExpiration = 43200;
    private String issuer = "shmanchkin";
    private String authoritiesClaimName = "authorities";
    private String accessCookieName = "access";
    private String accessCookiePath = "/";
    private String refreshCookieName = "refresh";
    private String refreshCookiePath = "/auth/refresh";
}
