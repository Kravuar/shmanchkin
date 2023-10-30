package net.kravuar.shmanchkin.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.kravuar.shmanchkin.application.props.JWTProps;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JWTUtils {
    private final Algorithm algorithm;
    private final JWTProps jwtProps;
    private final JWTVerifier verifier;

    public JWTUtils(Algorithm algorithm, JWTProps jwtProps) {
        this.algorithm = algorithm;
        this.jwtProps = jwtProps;
        this.verifier = JWT.require(algorithm)
                .withIssuer(jwtProps.getIssuer())
                .build();
    }

    public JWTCreator.Builder getJWTBuilder(String subject, List<? extends GrantedAuthority> authorities, long expirationTime) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date().toInstant().plusSeconds(expirationTime))
                .withClaim(jwtProps.getAuthoritiesClaimName(), authorities)
                .withIssuer(jwtProps.getIssuer());
    }

    public String sign(JWTCreator.Builder builder) {
        return builder.sign(algorithm);
    }

    public DecodedJWT decode(String token) {
        if (token != null && token.startsWith(jwtProps.getTokenPrefix())) {
            String trimmed = token.substring(jwtProps.getTokenPrefix().length());
            return verifier.verify(trimmed);
        } else throw new JWTDecodeException("Отсутствует JWT токен.");
    }
}