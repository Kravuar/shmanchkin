package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class GameLobbyNotFoundException extends RuntimeException {
    private final String lobbyName;

    public GameLobbyNotFoundException(String lobbyName) {
        super("Лобби с названием " + lobbyName + " не найдено.");
        this.lobbyName = lobbyName;
    }
}
