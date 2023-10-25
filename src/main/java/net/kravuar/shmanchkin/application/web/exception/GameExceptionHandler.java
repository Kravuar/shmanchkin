package net.kravuar.shmanchkin.application.web.exception;

import net.kravuar.shmanchkin.domain.model.exceptions.GameException;
import net.kravuar.shmanchkin.domain.model.exceptions.UserIsIdleException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GameExceptionHandler {

    @ExceptionHandler(GameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String gameExceptionHandler(GameException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(UserIsIdleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userIsIdleHandler(UserIsIdleException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }
}
