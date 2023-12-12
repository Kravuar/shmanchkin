package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;

@Getter
public class NotEnoughPlayersInLobbyException extends GameLobbyException {
    private final int playersRequired;

    public NotEnoughPlayersInLobbyException(GameLobby gameLobby, int playersRequired) {
        super(gameLobby, "В лобби с названием " + gameLobby.getLobbyName() + " не хватает игроков для старта, необходимо ещё " + playersRequired + ".");
        this.playersRequired = playersRequired;
    }
}
