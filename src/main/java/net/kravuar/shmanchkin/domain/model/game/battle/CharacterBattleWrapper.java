package net.kravuar.shmanchkin.domain.model.game.battle;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.cards.door.CharacterClass;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Curse;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Race;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.Wearable;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.List;

// Both forwarding and wrapper class (since no reusability expected)
@RequiredArgsConstructor
public class CharacterBattleWrapper implements Character {
    private final Character character;

//    TODO: fields like powerDelta, diceChance delta
//    TODO: override getters to take into account delta's
//    TODO: something else that i forgor (like curses)

    @Override
    public int getPower() {
        return character.getPower();
    }

    @Override
    public int getLevel() {
        return character.getLevel();
    }

    @Override
    public int getMaxCards() {
        return character.getMaxCards();
    }

    @Override
    public CharacterClass getCharacterClass() {
        return character.getCharacterClass();
    }

    @Override
    public Race getRace() {
        return character.getRace();
    }

    @Override
    public List<Card> getCardsInHand() {
        return character.getCardsInHand();
    }

    @Override
    public List<Wearable> getEquippedWearables() {
        return character.getEquippedWearables();
    }

    @Override
    public List<Curse> getActiveCurses() {
        return character.getActiveCurses();
    }

    @Override
    public void addCardToHand(Card card) {
        character.addCardToHand(card);
    }

    @Override
    public Card getCardFromHand(int index) {
        return character.getCardFromHand(index);
    }

    @Override
    public void equip(Wearable equipment) {
        character.equip(equipment);
    }

    @Override
    public void unequip(Wearable equipment) {
        character.unequip(equipment);
    }

    @Override
    public void addCurse(Curse curse) {
        character.addCurse(curse);
    }
}
