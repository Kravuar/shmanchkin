package net.kravuar.shmanchkin.domain.model.game.character;

import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.cards.door.CharacterClass;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Curse;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Race;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.Wearable;

import java.util.List;

public interface Character {
    int getPower();
    int getLevel();

    int getMaxCards();
    CharacterClass getCharacterClass();
    Race getRace();

    List<Card> getCardsInHand();
    List<Wearable> getEquippedWearables();
    List<Curse> getActiveCurses();

    void addCardToHand(Card card);
    Card getCardFromHand(int index);

    void equip(Wearable equipment);
    void unequip(Wearable equipment);

    void addCurse(Curse curse);
}
