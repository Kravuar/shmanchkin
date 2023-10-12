package net.kravuar.shmanchkin.application.web.exception;

import net.kravuar.shmanchkin.domain.model.exceptions.GameException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GameExceptionHandler {
//    Handling of irrecoverable exceptions happens here.
//    TODO: BETTER EXCEPTION RESPONSES

    @ExceptionHandler(GameException.class)
    @ResponseBody
    public String onGameNotFound(GameException exception) {
        return exception.getMessage();
    }
}
