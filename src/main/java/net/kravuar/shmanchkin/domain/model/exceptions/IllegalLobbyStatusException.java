package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class IllegalLobbyStatusException extends GameException {

    public IllegalLobbyStatusException(String lobbyName, GameLobby.LobbyStatus status) {
        super(lobbyName, "Выполнено действия для лобби с названием " + lobbyName + " с неподходящим статусом: " + status + ".");
    }
}
