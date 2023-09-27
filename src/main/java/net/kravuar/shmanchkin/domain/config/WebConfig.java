package net.kravuar.shmanchkin.domain.config;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.WebProps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.List;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig {
    private final WebProps webProps;

    @Bean
    public CorsWebFilter corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(webProps.getAllowedOrigins());
        configuration.setAllowedHeaders(List.of("Content-Type"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }
}
