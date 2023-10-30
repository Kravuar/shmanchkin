package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class ForbiddenActionException extends GameException {
    private final String action;

    public ForbiddenActionException(String lobbyName, String action) {
        super(lobbyName, "Вы не можете совершить действие '" + action + "' в лобби "+ lobbyName + ".");
        this.action = action;
    }
}
