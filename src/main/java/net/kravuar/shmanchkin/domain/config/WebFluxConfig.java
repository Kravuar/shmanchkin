package net.kravuar.shmanchkin.domain.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.JWTProps;
import net.kravuar.shmanchkin.application.props.WebProps;
import net.kravuar.shmanchkin.application.services.JWTUtils;
import net.kravuar.shmanchkin.domain.security.JWTAuthFilter;
import net.kravuar.shmanchkin.domain.security.JWTExtractor;
import net.kravuar.shmanchkin.domain.security.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebFluxConfig {
    private final WebProps webProps;
    private final JWTProps jwtProps;

    @Bean
    public WebFluxConfigurer corsFluxMappingConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins(webProps.getAllowedOrigins().toArray(String[]::new));
            }
        };
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JWTAuthFilter jwtAuthFilter) {
        http.authorizeExchange(config ->
            config
                    .pathMatchers(webProps.getUnauthenticatedPathMatchers().toArray(String[]::new))
                        .permitAll()
                    .anyExchange()
                        .authenticated()
        );
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.httpBasic(config -> config.authenticationEntryPoint(new HttpBasicServerAuthenticationEntryPoint() {
            @Override
            public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
                return Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().set("WWW-Authenticate", "BasicCustom");
                });
            }
        }));
        return http.build();
    }

    @Bean
    public JWTAuthFilter jwtAuthFilter(JWTExtractor jwtExtractor, PrincipalExtractor principalExtractor, JWTUtils jwtUtils) {
        return new JWTAuthFilter(jwtExtractor, jwtProps.getAccessCookieName(), principalExtractor, jwtUtils, jwtProps.getAuthoritiesClaimName(), new OrServerWebExchangeMatcher(
                webProps.getUnauthenticatedPathMatchers().stream()
                        .map(PathPatternParserServerWebExchangeMatcher::new)
                        .map(ServerWebExchangeMatcher.class::cast)
                        .toList()
        ));
    }
}
