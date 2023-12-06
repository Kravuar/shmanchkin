package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class GameLobbyNotFoundException extends GameLobbyException {

    public GameLobbyNotFoundException(String lobbyName) {
        super(lobbyName, "Лобби с названием " + lobbyName + " не найдено.");
    }
}
