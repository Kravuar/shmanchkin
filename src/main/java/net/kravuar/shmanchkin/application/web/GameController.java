package net.kravuar.shmanchkin.application.web;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games/actions")
@RequiredArgsConstructor
@Validated
public class GameController {
    private final GameService gameService;

    @PostMapping("/attemptEscape")
    public Mono<Boolean> attemptEscape() {
        return gameService.escapeBattle();
    }
}
