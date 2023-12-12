package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.exceptions.game.IllegalGameStageException;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Game {
//    TODO: create gameLoopManagingWrapper with scheduling.
    public enum Stage {
        IDLE,
        STARTED,
        PRE_BATTLE,
        BATTLE,
        POST_BATTLE,
        FINISHED
    }

    private final Map<String, Character> characters = new HashMap<>();
    @Getter
    protected Stage stage = Stage.IDLE;

    public Character getCharacter(String name) {
        return characters.get(name);
    }

    public void addCharacter(String name, Character character) {
        characters.put(name, character);
    }

    public boolean removeCharacter(String name) {
        return characters.remove(name) != null;
    }

    public void start() {
        if (stage != Stage.IDLE)
            throw new IllegalGameStageException(stage);
        advanceGameLoop();
    }

    public void advanceGameLoop() {
        switch (stage) {
            case IDLE -> {
                stage = Stage.STARTED;
                dealCards();
            }
            case PRE_BATTLE -> {
                stage = Stage.BATTLE;
                System.out.println("snos kabin");
            }
            default -> System.out.println("TBD");
        }
    }

    private void dealCards() {
        System.out.println("DEALING");
    }

//    TODO: return dice throw result or smth
    public boolean escape(Character character) {
        return false;
    }
}
