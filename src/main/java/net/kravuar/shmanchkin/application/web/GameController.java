package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.dto.CharacterDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games/actions")
@RequiredArgsConstructor
@Validated
public class GameController {
    private final GameService gameService;

    @Operation(
            summary = "Сбежать из битвы.",
            description = "Осуществляет попытку к бегству, если возможно."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Попытка осуществлена, получен результат попытки."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Пользователь не в игре или не в битве.")
    })
    @PostMapping("/attemptEscape")
    public Mono<Boolean> attemptEscape() {
        return gameService.escapeBattle();
    }

    @Operation(
            summary = "Сыграть карту.",
            description = "Использует карту в игре."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта использована."),
            @ApiResponse(responseCode = "400", description = "Невозможно использовать карту. Пользователь не в игре или сейчас неподходящий момент для использования карты.")
    })
    @PostMapping("/playCard")
    public Mono<Void> playCard(@RequestBody int inHandCardPosition) {
        return gameService.playCard(inHandCardPosition);
    }

    @Operation(
            summary = "Закончить ход.",
            description = "Заканчивает ход, передаёт ход следующему игроку."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ход закончен."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Пользователь не в игре или сейчас не его ход.")
    })
    @PostMapping("/endTurn")
    public Mono<Void> endTurn() {
        return gameService.endTurn();
    }

    @Operation(
            summary = "Информация всякая.",
            description = "Карточки, левел там."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Норм."),
            @ApiResponse(responseCode = "400", description = "Не норм.")
    })
    @PostMapping("/characterInfo")
    public Mono<CharacterDTO> characterInfo() {
        return gameService.getInfo();
    }
}
