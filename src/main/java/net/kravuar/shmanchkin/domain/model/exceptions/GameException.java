package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameException extends RuntimeException {
    private final String lobbyName;

    public GameException(String lobbyName, String message) {
        super(message);
        this.lobbyName = lobbyName;
    }
}
