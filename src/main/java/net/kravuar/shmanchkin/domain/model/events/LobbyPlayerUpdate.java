package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

@Getter
public class LobbyPlayerUpdate extends PlayerEvent {
    public enum LobbyPlayerAction {
        JOINED,
        LEFT
    }

    private final LobbyPlayerAction action;

    public LobbyPlayerUpdate(Game game, UserInfo userInfo, LobbyPlayerAction action) {
        super(game, userInfo);
        this.action = action;
    }
}
