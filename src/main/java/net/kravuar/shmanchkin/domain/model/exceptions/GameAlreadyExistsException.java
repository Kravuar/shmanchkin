package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameAlreadyExistsException extends GameException {

    public GameAlreadyExistsException(String lobbyName) {
        super(lobbyName, "Game with name " + lobbyName + " already exists.");
    }
}
