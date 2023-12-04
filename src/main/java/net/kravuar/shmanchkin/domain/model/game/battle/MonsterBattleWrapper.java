package net.kravuar.shmanchkin.domain.model.game.battle;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Monster;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.Collection;

// Both forwarding and wrapper class (since no reusability expected)
@RequiredArgsConstructor
public class MonsterBattleWrapper implements Monster {
    private final Monster monster;

//    TODO: fields like powerDelta, diceChance delta
//    TODO: override getters to take into account delta's
//    TODO: something else that i forgor

    @Override
    public int getImageId() {
        return monster.getImageId();
    }

    @Override
    public int getPower() {
        return monster.getPower();
    }

    @Override
    public int getRewardCount() {
        return monster.getRewardCount();
    }

    @Override
    public void updateBonuses(Collection<Character> characters) {
        monster.updateBonuses(characters);
    }

    @Override
    public void applyInitialSideEffects(Character victim) {
        monster.applyInitialSideEffects(victim);
    }

    @Override
    public void onDefeated(Character killer) {
        monster.onDefeated(killer);
    }

    @Override
    public void onDefeat(Character victim) {
        monster.onDefeat(victim);
    }

    @Override
    public boolean willIgnore(Character character) {
        return monster.willIgnore(character);
    }
}
