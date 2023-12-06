package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class GameLobbyIsFullException extends GameLobbyException {

    public GameLobbyIsFullException(String lobbyName) {
        super(lobbyName, "Лобби с названием " + lobbyName + " заполнено.");
    }
}
