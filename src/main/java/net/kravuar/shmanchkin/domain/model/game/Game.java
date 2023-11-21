package net.kravuar.shmanchkin.domain.model.game;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private final Map<String, Character> characters = new HashMap<>();
//    More like an automata state
//    @Getter
//    private GameStateStatus status;

    public void addCharacter(String name, Character character) {
        characters.put(name, character);
    }

    public boolean removeCharacter(String name) {
        return characters.remove(name) != null;
    }

    public void start() {
//        TODO: Other game init stuff
    }
}
