package net.kravuar.shmanchkin.application.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("game")
@Data
public class GameProps {
    private int autoCloseTimeout = 30;
}
