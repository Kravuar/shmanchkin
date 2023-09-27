package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameNotFoundException extends RuntimeException {
    private final String lobbyName;

    public GameNotFoundException(String lobbyName) {
        super("Game with name " + lobbyName + " not found.");
        this.lobbyName = lobbyName;
    }
}
