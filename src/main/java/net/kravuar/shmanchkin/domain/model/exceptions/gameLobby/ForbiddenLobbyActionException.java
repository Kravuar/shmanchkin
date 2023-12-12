package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class ForbiddenLobbyActionException extends GameLobbyException {
    private final String action;

    public ForbiddenLobbyActionException(String lobbyName, String action) {
        super(lobbyName, "Вы не можете совершить действие '" + action + "' в лобби " + lobbyName + ".");
        this.action = action;
    }
}
