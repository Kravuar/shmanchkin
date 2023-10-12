package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class UsernameTakenException extends GameException {
    private final String username;

    public UsernameTakenException(String lobbyName, String username) {
        super(lobbyName, "Username " + username + " already taken in lobby " + lobbyName + ".");
        this.username = username;
    }
}
