package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameIsFullException extends GameException {

    public GameIsFullException(String lobbyName) {
        super(lobbyName, "Game with name " + lobbyName + " is full.");
    }
}
