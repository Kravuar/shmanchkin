package net.kravuar.shmanchkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "net.kravuar.shmanchkin.application.props")
public class ShmanchkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShmanchkinApplication.class, args);
    }
}