package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class PlayerNotFoundException extends GameException {

    public PlayerNotFoundException(String lobbyName, String username) {
        super(lobbyName, "Игрок с именем " + username + " не найден в лобби " + lobbyName + ".");
    }
}
