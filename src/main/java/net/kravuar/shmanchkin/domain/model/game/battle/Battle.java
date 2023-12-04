package net.kravuar.shmanchkin.domain.model.game.battle;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.cards.door.Monster;
import net.kravuar.shmanchkin.domain.model.game.cards.treasure.OneTimeBonus;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Battle {
//    TODO: battle-local stats for each entity (player, monster). Maybe decorator (total power, escape chance...)
//    TODO: phases
//          player enters battle (outside of Battle class): takes door card
//                                if not monster: play monster from hand or take another door
//          before battle: apply all monsters onEnteredBattle and InitialSideEffects to player (and to new players, when joined)
//                         move monsters to ignorable (if they will ignore ALL players, so recheck when new player joined)
//                         apply (if applicable) players debuffs
//          mid battle: play bonus cards
//          after battle: check if all nonIgnorable monsters can be defeated
//    TODO: provide access to card decks (or just do all the card stuff on turn start/end)
//    TODO: Convert this to javadoc

    @Getter
    private final Collection<Monster> ignorableMonsters = new LinkedList<>();
    @Getter
    private final Collection<Monster> nonIgnorableMonsters = new LinkedList<>();
    @Getter
    private final Collection<Character> characters = new LinkedList<>();

    public Battle(Character character, Monster monster) {
        addCharacter(character);
        addMonster(monster);
    }

    public Collection<Monster> getAllMonsters() {
        return Stream.concat(
                nonIgnorableMonsters.stream(),
                ignorableMonsters.stream()
        ).collect(Collectors.toList());
    }

    public BattleResult finishBattle() {
        return getBattleResult();
    }

    public void playEffect(Character character, OneTimeBonus oneTimeCard) {
    }

    public void escape(Character character) {

    }

    public boolean addCharacter(Character character) {
//        TODO: Check if it can join the battle
//        TODO: Store in some wrapper that considers battle local stats (bonuses, curses)
        var added = characters.add(character);
        if (added) {
            getAllMonsters().forEach(monster -> {
                        monster.updateBonuses(characters);
                        monster.applyInitialSideEffects(character);
                    }
            );
            reorganizeMonsters();
        }

        return added;
    }

    public boolean addMonster(Monster monster) {
        if (getAllMonsters().contains(monster))
            return false;
//        TODO: Store in some wrapper that considers battle local stats (monster bonuses)
        if (willIgnore(monster))
            ignorableMonsters.add(monster);
        else
            nonIgnorableMonsters.add(monster);

        monster.updateBonuses(characters);
        characters.forEach(monster::applyInitialSideEffects);

        return true;
    }

    private boolean willIgnore(Monster monster) {
        return characters.stream()
                .allMatch(monster::willIgnore);
    }

    private void reorganizeMonsters() {
        for (var monster : getAllMonsters())
            if (willIgnore(monster)) {
                nonIgnorableMonsters.remove(monster);
                ignorableMonsters.add(monster);
            } else {
                ignorableMonsters.remove(monster);
                nonIgnorableMonsters.add(monster);
            }
    }

    private BattleResult getBattleResult() {
        var charactersTotalPower = characters.stream()
                .mapToInt(Character::getPower)
                .sum();
        var monstersTotalPower = nonIgnorableMonsters.stream()
                .mapToInt(Monster::getPower)
                .sum();
        return charactersTotalPower >= monstersTotalPower
                ? BattleResult.VICTORY
                : BattleResult.DEFEAT;
    }

    public enum BattleResult {
        VICTORY,
        DEFEAT
    }
}
