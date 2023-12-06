package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class UsernameTakenException extends GameLobbyException {
    private final String username;

    public UsernameTakenException(String lobbyName, String username) {
        super(lobbyName, "Имя пользователя " + username + " уже занято в лобби с названием " + lobbyName + ".");
        this.username = username;
    }
}
