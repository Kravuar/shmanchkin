package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.GameDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;

import java.util.Collection;

@Getter
@Setter
public class GameListFullUpdateDTO extends EventDTO {
    private Collection<GameDTO> games;

    public GameListFullUpdateDTO(Collection<Game> games) {
        super("game-full-update");
        this.games = games.stream().map(GameDTO::new).toList();
    }
}
