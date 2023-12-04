package net.kravuar.shmanchkin.domain.model.game.cards.door;

import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.Collection;

public interface Monster extends Door {
    int getPower();

    int getRewardCount();

    // Buff/debuff itself (e.g. if there's cleric - +3 power)
    void updateBonuses(Collection<Character> characters);

    // Side effects (e.g. if cleric - drop 1 item)
    void applyInitialSideEffects(Character victim);

    // Get treasures, other side effects
    void onDefeated(Character killer);

    // Apply the bad stuff (e.g. decrease level, drop cards...)
    void onDefeat(Character victim);

    // Check if it ignores character
    boolean willIgnore(Character character);
}
