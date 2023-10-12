package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameNotFoundException extends GameException {

    public GameNotFoundException(String lobbyName) {
        super(lobbyName, "Game with name " + lobbyName + " not found.");
    }
}
