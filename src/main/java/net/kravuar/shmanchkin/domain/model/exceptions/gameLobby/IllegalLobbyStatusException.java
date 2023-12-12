package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class IllegalLobbyStatusException extends GameLobbyException {
    private final GameLobby.LobbyStatus status;

    public IllegalLobbyStatusException(GameLobby gameLobby) {
        super(gameLobby, "Выполнено действия для лобби с названием " + gameLobby.getLobbyName() + " с неподходящим статусом: " + gameLobby.getLobbyStatus() + ".");
        this.status = gameLobby.getLobbyStatus();
    }
}
