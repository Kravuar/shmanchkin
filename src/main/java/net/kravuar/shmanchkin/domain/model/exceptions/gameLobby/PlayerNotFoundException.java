package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class PlayerNotFoundException extends GameLobbyException {

    public PlayerNotFoundException(String lobbyName, String username) {
        super(lobbyName, "Игрок с именем " + username + " не найден в лобби " + lobbyName + ".");
    }
}
