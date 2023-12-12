package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class AlreadyInGameLobbyException extends GameLobbyException {

    public AlreadyInGameLobbyException(GameLobby gameLobby) {
        super(gameLobby, "Уже в игре с названием " + gameLobby.getLobbyName() + ".");
    }
}
