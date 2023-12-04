package net.kravuar.shmanchkin.domain.model.game.cards.treasure;

import net.kravuar.shmanchkin.domain.model.game.character.Character;

public interface Wearable extends Treasure {
    enum Slot {
        HEAD,
        CHEST,
        SHOES,
        ONE_HAND,
        TWO_HANDS,
        OTHER
    }

    int getBonus();

    Slot getSlot();

    boolean isBig();

    boolean canEquip(Character character);
}
