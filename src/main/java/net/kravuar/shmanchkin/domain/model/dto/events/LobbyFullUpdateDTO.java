package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.PlayerDTO;
import net.kravuar.shmanchkin.domain.model.game.Player;

import java.util.Collection;

@Getter
@Setter
public class LobbyFullUpdateDTO extends EventDTO {
    private Collection<PlayerDTO> players;

    public LobbyFullUpdateDTO(Collection<Player> players) {
        super("players-full-update");
        this.players = players.stream().map(PlayerDTO::new).toList();
    }
}
