package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.LobbyStatus;

@Getter
public class IllegalLobbyStatusException extends GameException {

    public IllegalLobbyStatusException(String lobbyName, LobbyStatus status) {
        super(lobbyName, "Выполнено действия для лобби с названием " + lobbyName + " с неподходящим статусом: " + status + ".");
    }
}
