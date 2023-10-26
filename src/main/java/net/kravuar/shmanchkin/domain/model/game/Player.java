package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.MessageHandler;

@Getter
@Setter
public class Player {
    private String username;
    private Game game;
    private MessageHandler channelMessageHandler;

//    TODO: Character field with stuff like level, hand, armor and so on.

    public Player(String username, Game game) {
        this.username = username;
        this.game = game;
    }
}
