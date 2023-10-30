package net.kravuar.shmanchkin.domain.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private final String jwt;
    private final Object principal;

    public JWTAuthenticationToken(DecodedJWT jwt, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt.getToken();
        this.principal = principal;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
