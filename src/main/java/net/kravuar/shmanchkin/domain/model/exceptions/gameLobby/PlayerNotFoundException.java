package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class PlayerNotFoundException extends GameLobbyException {

    public PlayerNotFoundException(GameLobby gameLobby, String username) {
        super(gameLobby, "Игрок с именем " + username + " не найден в лобби " + gameLobby.getLobbyName() + ".");
    }
}
