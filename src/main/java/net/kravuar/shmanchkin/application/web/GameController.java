package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameEventDTO;
import net.kravuar.shmanchkin.domain.services.Username;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collection;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Validated
public class GameController {
    private final GameService gameService;

    @Operation(summary = "Список игр.", description = "Возвращает список активных игр.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список игр получен."),
    })
    @GetMapping("/gameList")
    public Collection<GameDTO> gameList() {
        return gameService.getGameList();
    }

    @Operation(summary = "Создание лобби.", description = "Создание лобби, с открытием SSE потока для игровых ивентов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра создана."),
            @ApiResponse(responseCode = "400", description = "Невозможно создать игру. Уже в игре."),
            @ApiResponse(responseCode = "400", description = "Невозможно создать игру. Имя лобби уже занято."),
    })
    @PostMapping("/create")
    public Flux<ServerSentEvent<GameEventDTO>> createLobby(@RequestBody GameFormDTO gameForm) {
        gameService.createGame(gameForm);
        return gameService.joinGame(gameForm.getLobbyName(), gameForm.getOwnerName());
    }

//    TODO: Переподключение
    @Operation(summary = "Подключение к лобби.", description = "Подключение к лобби, с открытием SSE потока для игровых ивентов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное подключение."),
            @ApiResponse(responseCode = "400", description = "Невозможно подключиться к игре. Игра не найдена."),
            @ApiResponse(responseCode = "400", description = "Невозможно подключиться к игре. Уже в игре."),
    })
    @GetMapping("/join/{lobbyName}/{username}")
    public Flux<ServerSentEvent<GameEventDTO>> joinLobby(@PathVariable String lobbyName, @PathVariable @Valid @Username String username) {
        return gameService.joinGame(lobbyName, username);
    }

    @Operation(summary = "Закрытие лобби.", description = "Закрытие лобби.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лобби успешно закрыто (если существовало)."),
    })
    @DeleteMapping("/close")
    public Boolean closeLobby() {
        return gameService.closeGame();
    }

    @Operation(summary = "Старт лобби.", description = "Запускает игру.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра запущена."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Лобби отсутствует."),
            @ApiResponse(responseCode = "400", description = "Невозможно подключиться к игре. Игра уже запущена."),
    })
    @PutMapping("/start")
    public void startGame() {
        gameService.startGame();
    }

//    Leaving is done via sse cancellation
}
