package net.kravuar.shmanchkin.domain.config;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.WebProps;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig {
    private final WebProps webProps;

    @Bean
    @SessionScope
    public UserInfo user() {
        return new UserInfo();
    }

    @Bean
    public CorsFilter corsFilter() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(webProps.getAllowedOrigins());
        configuration.setAllowedHeaders(List.of("Content-Type"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
