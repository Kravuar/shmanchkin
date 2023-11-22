package net.kravuar.shmanchkin.application.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("game")
@Data
public class GameProps {
    private int autoCloseTimeout = 30;
    private int lobbyMinPlayers = 1;
    private int lobbyMaxPlayers = 6;
}
