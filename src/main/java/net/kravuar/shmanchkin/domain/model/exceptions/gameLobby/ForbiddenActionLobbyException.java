package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class ForbiddenActionLobbyException extends GameLobbyException {
    private final String action;

    public ForbiddenActionLobbyException(String lobbyName, String action) {
        super(lobbyName, "Вы не можете совершить действие '" + action + "' в лобби " + lobbyName + ".");
        this.action = action;
    }
}
