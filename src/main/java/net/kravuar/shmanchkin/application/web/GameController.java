package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games/actions")
@RequiredArgsConstructor
@Validated
public class GameController {
    private final GameService gameService;

    @Operation(
            summary = "Старт лобби.",
            description = "Запускает игру. Нужно быть хостом."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра запущена."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Пользователь не в игре или игра уже запущена."),
            @ApiResponse(responseCode = "403", description = "Невозможно запустить игру. Пользователь не хост."),
    })
    @PutMapping("/start")
    public Mono<Void> startGame() {
        return gameService.startGame();
    }

    @PostMapping("/attemptEscape")
    public Mono<Boolean> attemptEscape() {
        return gameService.escapeBattle();
    }
}
