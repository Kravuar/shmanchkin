package net.kravuar.shmanchkin.domain.config;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kravuar.shmanchkin.application.services.AuthService;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.security.JWTExtractor;
import net.kravuar.shmanchkin.domain.security.PrincipalExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class WebFluxSecurityConfig {
    private final GameService gameService;

    @Value("classpath:${web.jwt.publicKeyResource}")
    private Resource publicKeyResource;

    @Value("classpath:${web.jwt.privateKeyResource}")
    private Resource privateKeyResource;

    @Bean
    @SneakyThrows
    public Algorithm algorithm() {
        try (InputStream publicKeyStream = publicKeyResource.getInputStream();
             InputStream privateKeyStream = privateKeyResource.getInputStream()) {

            byte[] publicKeyBytes = StreamUtils.copyToByteArray(publicKeyStream);
            byte[] privateKeyBytes = StreamUtils.copyToByteArray(privateKeyStream);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            return Algorithm.RSA256(publicKey, privateKey);
        }
    }

    @Bean
    public JWTExtractor jwtExtractor() {
        return (request, jwtName) -> {
            var cookie = request.getCookies().getFirst(jwtName);
            return cookie != null ? cookie.getValue() : "";
        };
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return decodedJWT -> {
            var uuid = UUID.fromString(decodedJWT.getClaim(AuthService.UUID_CLAIM).asString());
            var user = gameService.getActiveUser(uuid);
            return user == null
                    ? new UserInfo(uuid, decodedJWT.getSubject())
                    : user;
        };
    }
}
