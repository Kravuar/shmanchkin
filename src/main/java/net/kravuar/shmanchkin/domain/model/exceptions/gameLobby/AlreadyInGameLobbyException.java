package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class AlreadyInGameLobbyException extends GameLobbyException {

    public AlreadyInGameLobbyException(String lobbyName) {
        super(lobbyName, "Уже в игре с названием " + lobbyName + ".");
    }
}
