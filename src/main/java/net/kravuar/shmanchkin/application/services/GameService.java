package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.events.game.EscapeAttemptedDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.game.GameStageChangeDTO;
import net.kravuar.shmanchkin.domain.model.events.game.EscapeAttemptedEvent;
import net.kravuar.shmanchkin.domain.model.events.game.GameLobbyAwareGameEvent;
import net.kravuar.shmanchkin.domain.model.events.game.GameStageChangedEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.UserIsIdleException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GameService {
    private final UserService userService;

    public Mono<Boolean> escapeBattle() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());

                    var game = currentUser
                            .getSubscription()
                            .getGameLobby()
                            .getGame();

                    return Mono.just(game.escape(currentUser.getCharacter()));
                });
    }

    @EventListener
    protected void notifyGameStageChange(GameLobbyAwareGameEvent<GameStageChangedEvent> event) {
        var gameLobby = event.getGameLobby();

        var stageChange = new GameStageChangeDTO(event.getGameEvent().getStage());
        for (var user: gameLobby.getPlayers())
            user.send(stageChange);
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
