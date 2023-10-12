package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class GameAlreadyExistsException extends GameException {

    public GameAlreadyExistsException(String lobbyName) {
        super(lobbyName, "Game with name " + lobbyName + " already exists.");
    }
}
