package net.kravuar.shmanchkin.domain.model.game;

import net.kravuar.shmanchkin.domain.model.game.character.Character;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private final SubscribableChannel channel;
    private final Map<String, Character> characters = new HashMap<>();
    private Stage stage = Stage.PREPARATION;

    public Game(String name) {
        this.channel = MessageChannels.publishSubscribe(name).getObject();
    }

    public void addCharacter(String name, Character character) {
        characters.put(name, character);
    }

    public boolean removeCharacter(String name) {
        return characters.remove(name) != null;
    }

    public void start() {
//        TODO: Other game init stuff
    }

    public enum Stage {
        PREPARATION,
        START,
        PRE_BATTLE,
        BATTLE,
        POST_BATTLE,
        END
    }
}
