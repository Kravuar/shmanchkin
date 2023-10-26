package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class UsernameTakenException extends GameException {
    private final String username;

    public UsernameTakenException(String lobbyName, String username) {
        super(lobbyName, "Имя пользователя " + username + " уже занято в лобби с названием " + lobbyName + ".");
        this.username = username;
    }
}
