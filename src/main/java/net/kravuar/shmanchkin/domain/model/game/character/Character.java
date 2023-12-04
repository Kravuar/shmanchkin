package net.kravuar.shmanchkin.domain.model.game.character;

import net.kravuar.shmanchkin.domain.model.game.cards.Card;
import net.kravuar.shmanchkin.domain.model.game.cards.door.CharacterClass;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Curse;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Race;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.Wearable;

import java.util.Collection;

public interface Character {
    int getPower();

    int getLevel();

    int getMaxCards();

    CharacterClass getCharacterClass();

    Race getRace();

    Collection<Card> getCardsInHand();

    Collection<Wearable> getEquippedWearables();

    Collection<Curse> getActiveCurses();
}
