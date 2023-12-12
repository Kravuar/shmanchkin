package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class UsernameTakenException extends GameLobbyException {
    private final String username;

    public UsernameTakenException(GameLobby gameLobby, String username) {
        super(gameLobby, "Имя пользователя " + username + " уже занято в лобби с названием " + gameLobby.getLobbyName() + ".");
        this.username = username;
    }
}
