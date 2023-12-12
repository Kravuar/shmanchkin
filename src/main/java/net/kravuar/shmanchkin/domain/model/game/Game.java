package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.exceptions.game.GameIsActiveException;
import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Game {
    private final Map<String, Character> characters = new LinkedHashMap<>();
    @Getter
    private Character currentTurnCharacter = null;
    @Getter
    private boolean isActive = false;

    public Character getCharacter(String name) {
        return characters.get(name);
    }
    public void addCharacter(String name, Character character) {
        if (isActive)
            throw new GameIsActiveException(this);
        characters.put(name, character);
    }
    public boolean removeCharacter(String name) {
//        TODO: handle mid game removal
        return characters.remove(name) != null;
    }

    public void start() {
        if (isActive)
            throw new GameIsActiveException(this);
        dealCards();
//        TODO: validate start
//        TODO: other initialization
        currentTurnCharacter = getNextCharacter();
        isActive = true;
    }
    public void endTurn() {

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