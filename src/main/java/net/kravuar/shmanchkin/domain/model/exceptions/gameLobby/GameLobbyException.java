package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class GameLobbyException extends RuntimeException {
    private final String lobbyName;

    public GameLobbyException(String lobbyName, String message) {
        super(message);
        this.lobbyName = lobbyName;
    }
}
