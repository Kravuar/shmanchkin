package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.GameListUpdateAction;

@Getter
@Setter
public class GameListUpdateDTO extends EventDTO {
    private LobbyDTO game;

    public GameListUpdateDTO(Game game, GameListUpdateAction action) {
        super(action == GameListUpdateAction.CREATED
                ? "game-created"
                : "game-closed"
        );
        this.game = new LobbyDTO(game);
    }
}
