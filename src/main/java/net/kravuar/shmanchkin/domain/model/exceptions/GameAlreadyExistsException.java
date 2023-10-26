package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameAlreadyExistsException extends GameException {

    public GameAlreadyExistsException(String lobbyName) {
        super(lobbyName, "Лобби с названием " + lobbyName + " уже существует.");
    }
}
