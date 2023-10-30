package net.kravuar.shmanchkin.domain.config;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kravuar.shmanchkin.application.services.UserService;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.security.JWTExtractor;
import net.kravuar.shmanchkin.domain.security.PrincipalExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class WebFluxSecurityConfig {
    private final UserService userService;

    @Value("${web.jwt.publicKeyFile}")
    private String publicKeyPath;

    @Value("${web.jwt.privateKeyFile}")
    private String privateKeyPath;

    @Bean
    @SneakyThrows
    public Algorithm algorithm(ResourceLoader loader) {
        var publicKeyResource = loader.getResource(publicKeyPath);
        var privateKeyResource = loader.getResource(privateKeyPath);

        byte[] publicKeyBytes = Files.readAllBytes(publicKeyResource.getFile().toPath());
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyResource.getFile().toPath());

        var keyFactory = KeyFactory.getInstance("RSA");
        var publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        var privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        return Algorithm.RSA256(publicKey, privateKey);
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
            var uuid = UUID.fromString(decodedJWT.getClaim("uuid").asString());
            var user = userService.getActiveUser(uuid);
            return user == null
                    ? new UserInfo(uuid, decodedJWT.getSubject())
                    : user;
        };
    }
}
