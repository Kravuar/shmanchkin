package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;

@Getter
public class AlreadyInGameException extends GameException {

    public AlreadyInGameException(String lobbyName) {
        super(lobbyName, "Already playing in " + lobbyName + " lobby.");
    }
}
