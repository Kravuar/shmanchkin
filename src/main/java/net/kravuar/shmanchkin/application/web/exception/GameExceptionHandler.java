package net.kravuar.shmanchkin.application.web.exception;

import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GameExceptionHandler {
//    TODO: BETTER EXCEPTION RESPONSES

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseBody
    public String onGameNotFound(GameNotFoundException exception) {
        return "lobby not found: " + exception.getLobbyName();
    }

    @ExceptionHandler(GameIsFullException.class)
    @ResponseBody
    public String onGameNotFound(GameIsFullException exception) {
        return "lobby is full: " + exception.getLobbyName();
    }
}
