package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class UserIsIdleException extends RuntimeException {

    public UserIsIdleException(String message) {
        super("Пользователь неактивен. " + message);
    }

    public UserIsIdleException() {
        super("Пользователь неактивен.");
    }
}
