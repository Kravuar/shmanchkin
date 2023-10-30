package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.game.Game;

import java.util.Collection;

@Getter
@Setter
public class GameListFullUpdateDTO extends EventDTO {
    private Collection<LobbyDTO> games;

    public GameListFullUpdateDTO(Collection<Game> games) {
        super("game-full-update");
        this.games = games.stream().map(LobbyDTO::new).toList();
    }
}
