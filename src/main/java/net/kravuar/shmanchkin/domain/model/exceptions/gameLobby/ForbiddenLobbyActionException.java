package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class ForbiddenLobbyActionException extends GameLobbyException {
    private final String action;

    public ForbiddenLobbyActionException(GameLobby gameLobby, String action, String cause) {
        super(gameLobby, "Вы не можете совершить действие '" + action + "' в лобби " + gameLobby.getLobbyName() + ". " + cause);
        this.action = action;
    }
}
