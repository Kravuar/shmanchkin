package net.kravuar.shmanchkin.application.web;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.events.PlayerEvent;
import net.kravuar.shmanchkin.domain.model.events.TestEvent;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
    // TODO: Spring security + jwt for all players

    private final GameService gameService;

    @GetMapping("/join/{lobbyName}/{username}")
    public Flux<ServerSentEvent<GameEvent>> joinLobby(@PathVariable String lobbyName, @PathVariable String username) {
        return gameService.subscribe(lobbyName, username);
    }

//    TODO: pageable
    @GetMapping("/gameList")
    public List<GameDTO> gameList() {
        return gameService.getGameList().stream()
                .map(GameDTO::new)
                .toList();
    }

    @PostMapping("/create")
    public void createGame(@RequestBody GameFormDTO gameForm) {
        gameService.createGame(gameForm);
    }

//    TODO: Authorize the owner
    @DeleteMapping("/close/{lobbyName}")
    public Mono<Boolean> createGame(@PathVariable String lobbyName) {
        return Mono.just(gameService.close(lobbyName));
    }

    @PostMapping("/test/{lobbyName}/{username}")
    public void test(@RequestBody String message, @PathVariable String lobbyName, @PathVariable String username) {
        gameService.gameEventListener(new TestEvent(lobbyName, username, "test-event", message));
    }
}
