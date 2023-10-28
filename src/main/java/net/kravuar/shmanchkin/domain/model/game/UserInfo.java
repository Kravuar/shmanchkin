package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserInfo {
    @Getter
    @Setter
    private Player player;

    public boolean isIdle() {
        return player == null || player.isIdle();
    }

    public void toIdle() {
        if (!isIdle()) {
            player.toIdle();
            this.player = null;
        }
    }
}
