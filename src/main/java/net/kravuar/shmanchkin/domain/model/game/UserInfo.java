package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private Player player = null;

    public boolean isIdle() {
        return player == null;
    }

    public void toIdle() {
        this.player = null;
    }
}
