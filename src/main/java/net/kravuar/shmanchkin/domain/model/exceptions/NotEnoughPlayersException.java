package net.kravuar.shmanchkin.domain.model.exceptions;

import lombok.Getter;

@Getter
public class NotEnoughPlayersException extends GameException {

    public NotEnoughPlayersException(String lobbyName, int playersRequired) {
        super(lobbyName, "В лобби с названием " + lobbyName + " не хватает игроков для старта, необходимо ещё " + playersRequired + ".");
    }
}
