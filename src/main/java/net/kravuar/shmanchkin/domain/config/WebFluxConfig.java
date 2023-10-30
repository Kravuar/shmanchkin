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
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

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
                        .allowedOrigins(webProps.getAllowedOrigins().toArray(String[]::new));
            }
        };
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JWTAuthFilter jwtAuthFilter) {
        http.authorizeExchange(config -> {
//            TODO: that thing is fucked up for some reason
//            config.pathMatchers(webProps.getUnauthenticatedEndpoints().toArray(String[]::new))
//                    .permitAll();
//            config.anyExchange().authenticated();
            config.anyExchange().permitAll();
        });
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public JWTAuthFilter jwtAuthFilter(JWTExtractor jwtExtractor, PrincipalExtractor principalExtractor, JWTUtils jwtUtils) {
        return new JWTAuthFilter(jwtExtractor, jwtProps.getAccessCookieName(), principalExtractor, jwtUtils, jwtProps.getAuthoritiesClaimName(), new OrServerWebExchangeMatcher(
                webProps.getUnauthenticatedEndpoints().stream()
                        .map(PathPatternParserServerWebExchangeMatcher::new)
                        .map(ServerWebExchangeMatcher.class::cast)
                        .toList()
        ));
    }
}
