package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameIsActiveException extends GameException {

    public GameIsActiveException(String lobbyName) {
        super(lobbyName, "Game with name " + lobbyName + " is active.");
    }
}
