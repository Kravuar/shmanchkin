package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.CharacterDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.game.EscapeAttemptedDTO;
import net.kravuar.shmanchkin.domain.model.events.game.EscapeAttemptedEvent;
import net.kravuar.shmanchkin.domain.model.events.game.GameLobbyAwareGameEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.ForbiddenLobbyActionException;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.UserIsIdleException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameService {
    private final UserService userService;

    public Mono<Void> startGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenLobbyActionException(gameLobby, "Начать игру", "Вы не хост"));
                    gameLobby.start();
                    return Mono.empty();
                });
    }

    public Mono<Void> endTurn() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser
                            .getSubscription()
                            .getGameLobby();
                    var game = gameLobby.getGame();
                    var character = game.getCharacter(currentUser.getUsername());
                    if (!game.getCurrentTurnCharacter().equals(character))
                        return Mono.error(new ForbiddenLobbyActionException(gameLobby, "Закончить ход", "Не ваш ход"));
                    game.endTurn();
                    return Mono.empty();
                });
    }
    public Mono<Boolean> escapeBattle() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());

                    var game = currentUser
                            .getSubscription()
                            .getGameLobby()
                            .getGame();
                    var character = game.getCharacter(currentUser.getUsername());
                    return Mono.just(game.escapeBattle(character));
                });
    }
    public Mono<Void> playCard(int inHandCardPosition) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());

                    var game = currentUser
                            .getSubscription()
                            .getGameLobby()
                            .getGame();

                    var character = game.getCharacter(currentUser.getUsername());
                    var card = character.getCardsInHand().get(inHandCardPosition);
                    game.handleCard(card, character);

                    return Mono.empty();
                });
    }
    public Mono<CharacterDTO> getInfo() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());

                    var game = currentUser
                            .getSubscription()
                            .getGameLobby()
                            .getGame();

                    var character = game.getCharacter(currentUser.getUsername());
                    return Mono.just(new CharacterDTO(character));
                });
    }

    @EventListener
    protected void notifyEscapeAttempt(GameLobbyAwareGameEvent<EscapeAttemptedEvent> event) {
        var gameLobby = event.getGameLobby();

        var escapeAttempt = new EscapeAttemptedDTO(
                event.getGameEvent().getCharacter(),
                event.getGameEvent().isEscaped()
        );
        for (var user: gameLobby.getPlayers())
            user.send(escapeAttempt);
    }
}
