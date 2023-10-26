package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.dto.DetailedGameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.*;
import net.kravuar.shmanchkin.domain.services.Username;
import org.hibernate.validator.constraints.Length;
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

    @Operation(
            summary = "Список игр.",
            description = "Возвращает список активных игр."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список игр получен."),
    })
    @GetMapping
    public Collection<DetailedGameDTO> gameList() {
        return gameService.getGameList();
    }

    @Operation(
            summary = "Подписка на обновления списка лобби.",
            description = """
            <pre>
            Подписка на обновления списка лобби с открытием SSE потока.
            Подписка возможна на 3 ивента:
                game-created/game-closed,
                game-full-update.
            </pre>
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поток открыт.",
                    content = @Content(schema = @Schema(anyOf = {
                            GameListUpdateDTO.class,
                            GameListFullUpdateDTO.class
                    }))
            )
    })
    @GetMapping("/subscribe")
    public Flux<ServerSentEvent<GameListUpdateDTO>> subscribeToGameListUpdates() {
        return gameService.subscribeToGameListUpdates();
    }

    @Operation(
            summary = "Создание лобби.",
            description = "Создание лобби."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра создана."),
            @ApiResponse(responseCode = "400", description = "Невозможно создать игру. Пользователь уже в игре или имя игры уже занято."),
    })
    @PostMapping("/create")
    public void createLobby(@RequestBody GameFormDTO gameForm) {
        gameService.createGame(gameForm);
    }

//    TODO: Переподключение
    @Operation(
            summary = "Подключение к лобби.",
            description = """
            <pre>
            Подключение к лобби, с открытием SSE потока для игровых ивентов.
            Возможны следующие ивенты:
                На всех стадиях:
                    player-message
                В стадии ожидания:
                    player-connected/player-disconnected
                    players-full-update
                    player-kicked # Приходит только кикнутому игроку (вместе с остальными ивентами).
                В стадии игры:
                    ...
            </pre>
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное подключение.",
                    content = @Content(schema = @Schema(anyOf = {
                            MessageDTO.class,
                            LobbyUpdateDTO.class,
                            LobbyFullUpdateDTO.class,
                            KickedDTO.class
                    }))
            ),
            @ApiResponse(responseCode = "400", description = "Невозможно подключиться к игре. Игра не найдена или пользователь уже в игре."),
    })
    @GetMapping("/join/{lobbyName}/{username}")
    public Flux<ServerSentEvent<EventDTO>> joinLobby(@PathVariable String lobbyName, @PathVariable @Valid @Username String username) {
        return gameService.joinGame(lobbyName, username);
    }

    @Operation(
            summary = "Закрытие лобби.",
            description = "Закрытие лобби."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лобби успешно закрыто (если существовало)."),
    })
    @DeleteMapping("/close")
    public void closeLobby() {
        gameService.closeGame();
    }

    @Operation(
            summary = "Старт лобби.",
            description = "Запускает игру."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра запущена."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Лобби отсутствует или игра уже запущена."),
    })
    @PutMapping("/start")
    public void startGame() {
        gameService.startGame();
    }

    @Operation(
            summary = "Отправить сообщение.",
            description = "Отправляет сообщение в чат текущей игры."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сообщение отправлено."),
            @ApiResponse(responseCode = "400", description = "Пользователь не в игре или пустое сообщение."),
    })
    @PostMapping("/sendMessage")
    public void sendMessage(@NotNull @Length(min = 1) String message) {
        gameService.sendMessage(message);
    }

    @Operation(
            summary = "Исключить игрока.",
            description = "Исключает игрока из игры. (нужно быть хостом)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игрок исключён."),
            @ApiResponse(responseCode = "400", description = "Пользователь либо не в игре, либо не хост, либо игрок с таким именем не найден."),
    })
    @PutMapping("/kickPlayer")
    public void kickPlayer(String username) {
        gameService.kickPlayer(username);
    }
//    Leaving is done via sse cancellation
}
