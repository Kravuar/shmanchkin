package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class GameLobbyIsFullException extends GameLobbyException {

    public GameLobbyIsFullException(GameLobby gameLobby) {
        super(gameLobby, "Лобби с названием " + gameLobby.getLobbyName() + " заполнено.");
    }
}
