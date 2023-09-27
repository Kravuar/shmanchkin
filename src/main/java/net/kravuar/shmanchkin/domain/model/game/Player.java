package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;

@Getter
public class Player {
    private final String username;
//  hand, character ...

    public Player(String username) {
        this.username = username;
    }
}
