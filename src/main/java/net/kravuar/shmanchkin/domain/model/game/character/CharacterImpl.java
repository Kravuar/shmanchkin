package net.kravuar.shmanchkin.domain.model.game.character;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.cards.door.CharacterClass;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Curse;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Race;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.Wearable;

import java.util.Collection;
import java.util.LinkedList;

// TODO: Having an interface and Impl is bad
@Getter
public class CharacterImpl implements Character {
    //    TODO: externalize this values?
    private int level = 1;
    private int maxCards = 6;

    private CharacterClass characterClass;
    private Race race;

//    TODO: unmodifiable getters
    private final Collection<Card> cardsInHand = new LinkedList<>();
    private final Collection<Wearable> equippedWearables = new LinkedList<>();
    private final Collection<Curse> activeCurses = new LinkedList<>();

    @Override
    public int getPower() {
        return level + equippedWearables.stream()
                .mapToInt(Wearable::getBonus)
                .sum();
    }

    @Override
    public void addCardToHand(Card card) {
//        TODO: throw something if cant take cards
    }
    @Override
    public void equip(Wearable equipment) {
//        TODO: throw something if cant equip
    }
    @Override
    public void unequip(Wearable equipment) {
//        TODO: throw something if cant unequip
    }

    @Override
    public void addCurse(Curse curse) {
    }
}
