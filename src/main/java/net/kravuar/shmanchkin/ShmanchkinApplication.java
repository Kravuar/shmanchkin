package net.kravuar.shmanchkin;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Shmanchkin API", version = "0.0.1", description = "Yeah, API."))
@ConfigurationPropertiesScan(basePackages = "net.kravuar.shmanchkin.application.props")
public class ShmanchkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShmanchkinApplication.class, args);
    }
}