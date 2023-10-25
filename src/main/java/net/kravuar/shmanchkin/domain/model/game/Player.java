package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String username;
//  level, hand, character ...

    public Player(String username) {
        this.username = username;
    }
}
