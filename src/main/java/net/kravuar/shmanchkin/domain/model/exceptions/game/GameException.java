package net.kravuar.shmanchkin.domain.model.exceptions.game;

import lombok.Getter;

@Getter
public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }
}
