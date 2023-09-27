package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameIsFullException extends RuntimeException {
    private final String lobbyName;

    public GameIsFullException(String lobbyName) {
        super("Game with name " + lobbyName + " is full.");
        this.lobbyName = lobbyName;
    }
}
