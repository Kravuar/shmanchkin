package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.GameListUpdateAction;

@Getter
@Setter
public class GameListUpdateDTO extends EventDTO {
    private GameDTO game;

    public GameListUpdateDTO(Game game, GameListUpdateAction action) {
        super(action == GameListUpdateAction.CREATED
                ? "game-created"
                : "game-closed"
        );
        this.game = new GameDTO(game);
    }
}
