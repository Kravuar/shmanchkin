package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.FullLobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

import java.util.Collection;

@Getter
@Setter
public class LobbyListFullUpdateDTO extends EventDTO {
    private Collection<FullLobbyDTO> games;

    public LobbyListFullUpdateDTO(Collection<GameLobby> gameLobbies) {
        super("game-full-update");
        this.games = gameLobbies.stream().map(FullLobbyDTO::new).toList();
    }
}
