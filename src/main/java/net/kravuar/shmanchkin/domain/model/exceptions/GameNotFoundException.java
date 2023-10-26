package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class GameNotFoundException extends GameException {

    public GameNotFoundException(String lobbyName) {
        super(lobbyName, "Лобби с названием " + lobbyName + " не найдено.");
    }
}
