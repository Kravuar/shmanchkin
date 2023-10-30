package net.kravuar.shmanchkin.domain.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.JWTUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JWTAuthFilter implements WebFilter {
    private final JWTExtractor jwtExtractor;
    private final String jwtName;
    private final PrincipalExtractor principalExtractor;
    private final JWTUtils jwtUtils;
    private final String authoritiesClaimName;
    private final ServerWebExchangeMatcher ignoringMatcher;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return ignoringMatcher.matches(exchange).flatMap(matchResult -> {
            if (matchResult.isMatch()) return chain.filter(exchange);
            return processJWT(exchange, chain);
        });
    }

    private Mono<Void> processJWT(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        try {
            var request = exchange.getRequest();
            var decodedJWT = jwtUtils.decode(jwtExtractor.extract(request, jwtName));
            var principal = principalExtractor.extract(decodedJWT);
            var authorities = Arrays.stream(decodedJWT.getClaim(authoritiesClaimName).asArray(String.class))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            var token = new JWTAuthenticationToken(decodedJWT, principal, authorities);
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token));
        } catch (JWTVerificationException exception) {
            return chain.filter(exchange);
        }
    }
}
