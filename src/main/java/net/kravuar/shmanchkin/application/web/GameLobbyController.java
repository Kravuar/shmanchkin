package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameLobbyService;
import net.kravuar.shmanchkin.domain.model.dto.FullLobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.gameLobby.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Validated
public class GameLobbyController {
    private final GameLobbyService gameLobbyService;

    @Operation(
            summary = "Список игр.",
            description = "Возвращает список активных игр."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список игр получен."),
    })
    @GetMapping
    public Flux<FullLobbyDTO> lobbyList() {
        return gameLobbyService.getLobbyList();
    }

    @Operation(
            summary = "Подписка на обновления списка лобби.",
            description = """
                    <pre>
                    Подписка на обновления списка лобби с открытием SSE потока.
                    Подписка возможна на 3 ивента:
                        gameLobby-created/gameLobby-closed,
                        gameLobby-full-update.
                    </pre>
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поток открыт.",
                    content = @Content(schema = @Schema(anyOf = {
                            LobbyListUpdateDTO.class,
                            LobbyListFullUpdateDTO.class
                    }))
            )
    })
    @GetMapping("/subscribe")
    public Flux<ServerSentEvent<EventDTO>> subscribeToLobbyListUpdates() {
        return gameLobbyService.subscribeToLobbyListUpdates();
    }

    @Operation(
            summary = "Создание лобби.",
            description = "Создание лобби, подключится нужно отдельно. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра создана."),
            @ApiResponse(responseCode = "400", description = "Невозможно создать игру. Пользователь уже в игре или имя игры уже занято."),
    })
    @PostMapping("/create")
    public Mono<Void> createLobby(@RequestBody GameFormDTO gameForm) {
        return gameLobbyService.createGame(gameForm);
    }

    //    TODO: Переподключение
    @Operation(
            summary = "Подключение к лобби.",
            description = """
                    <pre>
                    Подключение к лобби, с открытием SSE потока для игровых ивентов.
                    Возможны следующие ивенты:
                        На всех стадиях:
                            userInfo-message
                            gameLobby-status-change
                        В стадии ожидания:
                            userInfo-connected/userInfo-disconnected
                            userInfos-full-update
                            userInfo-kicked # Приходит только кикнутому игроку (вместе с остальными ивентами).
                        В стадии игры:
                            ...
                    </pre>
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное подключение.",
                    content = @Content(schema = @Schema(anyOf = {
                            MessageDTO.class,
                            LobbyStatusChangedDTO.class,
                            LobbyPlayersUpdateDTO.class,
                            LobbyPlayersFullUpdateDTO.class,
                    }))
            ),
            @ApiResponse(responseCode = "400", description = "Невозможно подключиться к игре. Игра не найдена, либо пользователь уже в игре, либо игра заполнена, либо имя игрока уже занято, либо игра уже начата."),
    })
    @GetMapping("/join/{lobbyName}")
    public Flux<ServerSentEvent<EventDTO>> joinLobby(@PathVariable String lobbyName) {
        return gameLobbyService.joinGame(lobbyName);
    }

    @Operation(
            summary = "Закрытие лобби.",
            description = "Закрытие лобби текущего пользователя. Нужно быть хостом."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лобби успешно закрыто."),
            @ApiResponse(responseCode = "400", description = "Невозможно запустить игру. Пользователь не в игре."),
            @ApiResponse(responseCode = "403", description = "Невозможно запустить игру. Пользователь не хост.")
    })
    @DeleteMapping("/close")
    public Mono<Void> closeGame() {
        return gameLobbyService.closeGame();
    }

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
        return gameLobbyService.startGame();
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
    public Mono<Void> sendMessage(@RequestBody @NotBlank @Length(min = 1) String message) {
        return gameLobbyService.sendMessage(message);
    }

    @Operation(
            summary = "Исключить игрока.",
            description = "Исключает игрока из игры. Нужно быть хостом."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игрок исключён."),
            @ApiResponse(responseCode = "400", description = "Пользователь либо не в игре, либо игрок с таким именем не найден."),
            @ApiResponse(responseCode = "403", description = "Пользователь не хост."),
    })
    @PutMapping("/kickPlayer")
    public Mono<Void> kickPlayer(String username) {
        return gameLobbyService.kickPlayer(username);
    }

    @Operation(
            summary = "Покинуть игру.",
            description = "Выходит из текущей игры. Также можно выйти завершив SSE со стороны клиента."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игра покинута."),
    })
    @PutMapping("/leave")
    public Mono<Void> leave() {
        return gameLobbyService.leaveGame();
    }

    @Operation(
            summary = "Получить информацию о лобби.",
            description = "Основная информация о лобби."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация получена."),
            @ApiResponse(responseCode = "400", description = "Не в игре."),
    })
    @GetMapping("/info")
    public Mono<FullLobbyDTO> getLobbyInfo() {
        return gameLobbyService.getLobbyInfo();
    }
}
