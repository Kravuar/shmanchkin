package net.kravuar.shmanchkin.domain.model.game.character;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.cards.door.CharacterClass;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Curse;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Race;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.Wearable;

import java.util.Collection;
import java.util.LinkedList;

@Getter
public class CharacterImpl implements Character {
    //    TODO: externalize default values?
    private int level = 1;
    private int maxCards = 6;

    private CharacterClass characterClass;
    private Race race;

    private final Collection<Card> cardsInHand = new LinkedList<>();
    private final Collection<Wearable> equippedWearables = new LinkedList<>();
    private final Collection<Curse> activeCurses = new LinkedList<>();

    @Override
    public int getPower() {
        return level + equippedWearables.stream()
                .mapToInt(Wearable::getBonus)
                .sum();
    }

    ;
}
