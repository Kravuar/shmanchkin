package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class GameLobbyAlreadyExistsException extends GameLobbyException {

    public GameLobbyAlreadyExistsException(String lobbyName) {
        super(lobbyName, "Лобби с названием " + lobbyName + " уже существует.");
    }
}
