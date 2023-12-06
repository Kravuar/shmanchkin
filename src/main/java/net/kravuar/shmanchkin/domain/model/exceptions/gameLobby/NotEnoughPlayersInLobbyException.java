package net.kravuar.shmanchkin.domain.model.exceptions.gameLobby;

import lombok.Getter;

@Getter
public class NotEnoughPlayersInLobbyException extends GameLobbyException {

    public NotEnoughPlayersInLobbyException(String lobbyName, int playersRequired) {
        super(lobbyName, "В лобби с названием " + lobbyName + " не хватает игроков для старта, необходимо ещё " + playersRequired + ".");
    }
}
