package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class UserIsIdleException extends RuntimeException {

    public UserIsIdleException() {
        super("Вы не в игре.");
    }
}
