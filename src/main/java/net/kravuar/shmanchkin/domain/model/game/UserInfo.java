package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private String username;
    private Game game = null;

    public UserInfo(String username) {
        this.username = username;
    }

    public boolean isIdle() {
        return game == null;
    }

    public void toIdle() {
        this.username = null;
        this.game = null;
    }
}
