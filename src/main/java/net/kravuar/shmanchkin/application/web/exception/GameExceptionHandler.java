package net.kravuar.shmanchkin.application.web.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.kravuar.shmanchkin.domain.model.exceptions.game.GameException;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.ForbiddenLobbyActionException;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.GameLobbyException;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.UserIsIdleException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GameExceptionHandler {
    @ExceptionHandler(ForbiddenLobbyActionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenLobbyAction(ForbiddenLobbyActionException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAccessDenied(AccessDeniedException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(JWTVerificationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String jwtExceptionHandler(JWTVerificationException exception) {
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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public List<String> handleConstraintViolation(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    @ExceptionHandler(GameLobbyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleGameLobbyException(GameLobbyException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(GameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleGameException(GameException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String otherExceptionHandler(Exception exception) {
        log.error(exception.getMessage(), exception);
        return exception.getMessage();
    }
}
