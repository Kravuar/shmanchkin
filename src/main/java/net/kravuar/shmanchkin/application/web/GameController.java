package net.kravuar.shmanchkin.application.web;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.GameService;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
//    TODO: Add validation to request params and stuff
    private final GameService gameService;

//    TODO: pageable
    @GetMapping("/gameList")
    public List<GameDTO> gameList() {
        return gameService.getGameList().stream()
                .map(GameDTO::new)
                .toList();
    }

    @PostMapping("/create")
    public void createLobby(@RequestBody GameFormDTO gameForm) {
        System.out.println("jopa");
        gameService.createGame(gameForm);
    }

    @GetMapping("/join/{lobbyName}/{username}")
    public Flux<ServerSentEvent<GameEvent>> joinLobby(@PathVariable String lobbyName, @PathVariable String username) {
        return gameService.joinGame(lobbyName, username);
    }

    @DeleteMapping("/close")
    public Boolean closeLobby() {
        return gameService.closeGame();
    }

//    Leaving is done via sse cancellation

    @GetMapping("/test/{username}")
    public UserInfo test(@PathVariable String username) {
        var currentUser = gameService.getCurrentUser();
        if (currentUser.getUsername() != null)
            currentUser.setUsername(currentUser.getUsername() + 'a');
        else
            currentUser.setUsername(username);
        return new UserInfo(currentUser.getUsername());
    }
}
