package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class GameLobbyAlreadyExistsException extends GameLobbyException {
    private final String lobbyName;

    public GameLobbyAlreadyExistsException(GameLobby gameLobby) {
        super(gameLobby, "Лобби с названием " + gameLobby.getLobbyName() + " уже существует.");
        this.lobbyName = gameLobby.getLobbyName();

    }
}
