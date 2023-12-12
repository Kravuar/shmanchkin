package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class GameLobbyException extends RuntimeException {
    private final GameLobby gameLobby;

    public GameLobbyException(GameLobby gameLobby, String message) {
        super(message);
        this.gameLobby = gameLobby;
    }
}
