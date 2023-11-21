package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.game.LobbyListUpdateAction;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;

@Getter
@Setter
public class GameListUpdateDTO extends EventDTO {
    private LobbyDTO game;

    public GameListUpdateDTO(GameLobby gameLobby, LobbyListUpdateAction action) {
        super(action == LobbyListUpdateAction.CREATED
                ? "game-created"
                : "game-closed"
        );
        this.game = new LobbyDTO(gameLobby);
    }
}
