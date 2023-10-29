package net.kravuar.shmanchkin.domain.config;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.WebProps;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebFluxConfig {
    private final WebProps webProps;

    @Bean
    @SessionScope
    public UserInfo user() {
        return new UserInfo();
    }

    @Bean
    public WebFluxConfigurer corsFluxMappingConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(webProps.getAllowedOrigins().toArray(String[]::new));
            }
        };
    }
}
