package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.exceptions.game.IllegalGameStageException;
import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Game {
    public enum TurnStage {
        PRE_BATTLE,
        BATTLE,
        POST_BATTLE,
    }

    @Getter
    protected TurnStage turnStage = null;
    private final Map<String, Character> characters = new LinkedHashMap<>();
    private Character currentTurnCharacter = null;

    public Character getCharacter(String name) {
        return characters.get(name);
    }
    public void addCharacter(String name, Character character) {
        if (turnStage != null)
            throw new IllegalGameStageException(turnStage);
        characters.put(name, character);
    }
    public boolean removeCharacter(String name) {
        return characters.remove(name) != null;
    }

    public void start() {
        if (turnStage != null)
            throw new IllegalGameStageException(turnStage);
        dealCards();
//        TODO: validate start
        currentTurnCharacter = getNextCharacter();
//        TODO: other initialization
        turnStage = TurnStage.PRE_BATTLE;
    }

    public void advanceTurnStage() {
        switch (turnStage) {
            case PRE_BATTLE -> {
                turnStage = TurnStage.BATTLE;
                System.out.println("snos kabin");
            }
            case POST_BATTLE -> {
//                TODO: End turn, pass to the next
            }
            default -> System.out.println("TBD");
        }
    }
    public boolean escapeBattle(Character character) {
//    TODO: return dice throw result or smth
        return false;
    }
    public void handleCard(Card card, Character character) {
//        TODO: switch for different card types
//        MONSTER -> check if in battle -> add to battle
//        EQUIPMENT -> call character.equip(equipment)
    }

    private Character getNextCharacter() {
        var charactersList = new ArrayList<>(characters.values());
        if (currentTurnCharacter == null)
            return charactersList.get(0);
        var nextIndex = charactersList.indexOf(currentTurnCharacter) % characters.size();
        return charactersList.get(nextIndex);
    }
    private void dealCards() {
        System.out.println("DEALING");
    }
}