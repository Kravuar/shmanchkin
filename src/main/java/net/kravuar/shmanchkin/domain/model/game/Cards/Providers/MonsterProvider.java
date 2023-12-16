package net.kravuar.shmanchkin.domain.model.game.Cards.Providers;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.Monster;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.MonsterCatch;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.MonsterPlay;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;

public class MonsterProvider implements Deck.Provider {
    @Override
    public ArrayList<Card> GetCards() {
        ArrayList<Card> cards = new ArrayList<>();

        // Бигфут
        ArrayList<Character.Race> races = new ArrayList<>();
        races.add(Character.Race.dwarf);
        races.add(Character.Race.halfling);
        MonsterPlay bigFootPlay = RaceBonusPlay(races, 3);
        MonsterCatch bigFootCatch = enemy -> {
            enemy.helmet = null;
        };

        Monster bigFoot = new Monster(12, 3);
        bigFoot.setName("Бигфут");
        bigFoot.setPlay(bigFootPlay);
        bigFoot.setCatchUp(bigFootCatch);
        cards.add(bigFoot);

        // Гик
        ArrayList<Character.Class> classes = new ArrayList<>();
        classes.add(Character.Class.warrior);
        MonsterPlay gicPlay = ClassBonusPlay(classes, 6);

        MonsterCatch gicCatch = enemy -> {
            enemy.setCur_class(null);
            enemy.setRace(null);
        };
        Monster gic = new Monster(6, 2);
        gic.setName("Гикающий гик");
        gic.setPlay(gicPlay);
        gic.setCatchUp(gicCatch);
        cards.add(gic);

        // Бледные братья
        MonsterCatch brothersCatch = enemy -> {
            if (enemy.getLevel() <= 3) {
                enemy.resetLevel(); // сброс уровня на первый
            }
        };
        Monster brothers = new Monster(16, 4);
        brothers.setName("Братья");
        brothers.setCatchUp(brothersCatch);

        cards.add(brothers);

        // Молотая красотка
        ArrayList<Character.Class> classes2 = new ArrayList<>();
        classes2.add(Character.Class.cleric);
        MonsterPlay beautyPlay = ClassBonusPlay(classes2, 3);
        MonsterCatch beautyCatch = enemy -> {
            enemy.decreaseLevel(-1);
        };
        Monster beauty = new Monster(1, 1);
        beauty.setName("Крысавица");
        beauty.setPlay(beautyPlay);
        beauty.setCatchUp(beautyCatch);

        cards.add(beauty);

        return cards;
    }

    static private MonsterPlay ClassBonusPlay(ArrayList<Character.Class> personClasses, Integer bonus) {
        return new MonsterPlay() {
            @Override
            public boolean Condition(Character enemy) {
                boolean predicate = false;
                Character.Class cur_class = enemy.getCur_class();
                for (Character.Class personClass : personClasses) {
                    predicate |= (cur_class == personClass);
                }
                return predicate;
            }
            @Override
            public void Play(Monster monster, Character enemy) {
                if (Condition(enemy)) {
                    monster.ChangeAdditionalPower(bonus);
                }
            }
            @Override
            public void Reverse(Monster monster, Character enemy) {
                if (Condition(enemy)) {
                    monster.ChangeAdditionalPower(-bonus);
                }
            }
        };
    }

    static private MonsterPlay RaceBonusPlay(ArrayList<Character.Race> personRaces, Integer bonus) {
        return new MonsterPlay() {
            @Override
            public boolean Condition(Character enemy) {
                boolean predicate = false;
                Character.Race cur_race = enemy.getRace();
                for (Character.Race race : personRaces) {
                    predicate |= (cur_race == race);
                }
                return predicate;
            }
            @Override
            public void Play(Monster monster, Character enemy) {
                if (Condition(enemy)) {
                    monster.ChangeAdditionalPower(bonus);
                }
            }
            @Override
            public void Reverse(Monster monster, Character enemy) {
                if (Condition(enemy)) {
                    monster.ChangeAdditionalPower(-bonus);
                }
            }
        };
    }
}
