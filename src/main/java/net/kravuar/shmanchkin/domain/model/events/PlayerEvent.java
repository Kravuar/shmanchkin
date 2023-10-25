package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

@Getter
public class PlayerEvent extends GameEvent {
    private final UserInfo userInfo;

    public PlayerEvent(Game game, UserInfo userInfo) {
        super(game);
        this.userInfo = userInfo;
    }
}
