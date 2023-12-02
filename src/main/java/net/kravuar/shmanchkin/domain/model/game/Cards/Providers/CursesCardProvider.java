package net.kravuar.shmanchkin.domain.model.game.Cards.Providers;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Curses.CurseCard;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Curses.CursePlay;
import net.kravuar.shmanchkin.domain.model.game.Gear.WearableGear;
import net.kravuar.shmanchkin.domain.model.game.Person.Person;

import java.util.ArrayList;

public class CursesCardProvider implements Deck.Provider {
    @Override
    public ArrayList<Card> GetCards() {
        var cards = new ArrayList<Card>();

        var gnusCurse = new CurseCard("Невыносимо гнусное проклятие");

        gnusCurse.setPlay(new CursePlay() {
            @Override
            public void Play(CurseCard card, Person target) {
                System.out.printf("Играю %s\n", card.getName());
                int maxPower = -1;
                int maxPowerSlot = -1;
                if (target.helmet != null) {
                    if (target.helmet.getPower() > maxPower) {
                        maxPowerSlot = 0;
                        maxPower = target.helmet.getPower();
                    }
                }
                if (target.body != null) {
                    if (target.body.getPower() > maxPower) {
                        maxPowerSlot = 1;
                        maxPower = target.body.getPower();
                    }
                }
                if (target.legs != null) {
                    if (target.legs.getPower() > maxPower) {
                        maxPowerSlot = 2;
                        maxPower = target.legs.getPower();
                    }
                }
                WearableGear maxOther = null;
                for (var other : target.others) {
                    if (other.getPower() > maxPower) {
                        maxPower = other.getPower();
                        maxOther = other;
                        maxPowerSlot = 3;
                    }
                }
                switch (maxPowerSlot) {
                    case 0: {
                        target.helmet = null;
                        System.out.println("Lose helmet");
                        break;
                    }
                    case 1: {
                        target.body = null;
                        System.out.println("Lose body");
                        break;
                    }
                    case 2: {
                        target.legs = null;
                        System.out.println("Lose legs");
                        break;
                    }
                    case 3: {
                        target.others.remove(maxOther);
                        System.out.println("Lose other");
                        break;
                    }
                    default: {
                        System.out.println("Nothing to lose");
                    }
                }
            }

            @Override
            public void Leave(CurseCard card, Person target) {
            }
        });

        cards.add(gnusCurse);

        var loseHead = new CurseCard("Потеряй головняк");

        loseHead.setPlay(new CursePlay() {
            @Override
            public void Play(CurseCard card, Person target) {
                System.out.printf("Играю %s\n", card.getName());
                target.helmet = null;
            }

            @Override
            public void Leave(CurseCard card, Person target) {
            }
        });

        cards.add(loseHead);

        var loseLevel = new CurseCard("Потеряй уровень");

        loseLevel.setPlay(new CursePlay() {
            @Override
            public void Play(CurseCard card, Person target) {
                System.out.printf("Играю %s\n", card.getName());
                target.decreaseLevel(1);
            }

            @Override
            public void Leave(CurseCard card, Person target) {
            }
        });

        cards.add(loseLevel);

        var loseRace = new CurseCard("Потеряй расу");

        loseRace.setPlay(new CursePlay() {
            @Override
            public void Play(CurseCard card, Person target) {
                System.out.printf("Играю %s\n", card.getName());
                target.setRace(Person.Race.human);
            }

            @Override
            public void Leave(CurseCard card, Person target) {
            }
        });

        cards.add(loseRace);

        return cards;
    }
}
