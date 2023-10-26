package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class AlreadyInGameException extends GameException {

    public AlreadyInGameException(String lobbyName) {
        super(lobbyName, "Уже в игре с названием " + lobbyName + ".");
    }
}
