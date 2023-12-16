package net.kravuar.shmanchkin.domain.model.game.Cards.Providers;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards.ArmorWearableTreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards.WeaponWearableTreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards.WearablePlay;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards.WearableTreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Gear.ArmorGear;
import net.kravuar.shmanchkin.domain.model.game.Gear.WeaponGear;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;

public class WearableTreasureCardsProvider  implements Deck.Provider{
    public ArrayList<Card> GetCards() {
        ArrayList<Card> cards = new ArrayList<>();

        WearableTreasureCard tights = new ArmorWearableTreasureCard("Колготы великанской силы", 600, new ArmorGear(3, false, ArmorGear.Slot.Other));
        tights.setPlay(new WearablePlay() {
            @Override
            public void Wear(WearableTreasureCard wearable, Character target) {
                System.out.printf("Надеваю %s\n", wearable.getName());
                if (target.getCur_class() == Character.Class.warrior) {
                    System.out.println("ТЫ ВОИН АХАХАХАХАХАХ");
                    return;
                }
                ((ArmorWearableTreasureCard) wearable).wearArmor(target);
            }

            @Override
            public void UnWear(WearableTreasureCard wearable, Character target) {
                ((ArmorWearableTreasureCard) wearable).unWearArmor(target);
            }
        });
        cards.add(tights);

        WeaponWearableTreasureCard cheese = new WeaponWearableTreasureCard("Сыротерка умиротворения", 400, new WeaponGear(3, false, WeaponGear.Size.OneHand));
        cheese.setPlay(new WearablePlay() {
            @Override
            public void Wear(WearableTreasureCard wearable, Character target) {
                System.out.printf("Надеваю %s\n", wearable.getName());
                if (target.getCur_class() != Character.Class.cleric) {
                    System.out.println("ТЫ ДАЖЕ НЕ КЛЕРИК");
                    return;
                }
                ((WeaponWearableTreasureCard) wearable).addWeapon(target);
            }

            @Override
            public void UnWear(WearableTreasureCard wearable, Character target) {
                ((WeaponWearableTreasureCard) wearable).removeWeapon(target);
            }
        });
        cards.add(cheese);

        ArmorWearableTreasureCard bandana = new ArmorWearableTreasureCard("Бандана сволочизма", 400, new ArmorGear(3, false, ArmorGear.Slot.Helmet));
        bandana.setPlay(new WearablePlay() {
            @Override
            public void Wear(WearableTreasureCard wearable, Character target) {
                System.out.printf("Надеваю %s\n", wearable.getName());
                if (target.getRace() != Character.Race.human) {
                    System.out.println("ТЫ ДАЖЕ НЕ ЧЕЛОВЕК");
                    return;
                }
                ((ArmorWearableTreasureCard) wearable).wearArmor(target);
            }

            @Override
            public void UnWear(WearableTreasureCard wearable, Character target) {
                ((ArmorWearableTreasureCard) wearable).unWearArmor(target);
            }
        });
        cards.add(bandana);

        ArmorWearableTreasureCard leather_armor = new ArmorWearableTreasureCard("Кожаный прикид", 200, new ArmorGear(1, false, ArmorGear.Slot.Body));
        leather_armor.setPlay(new WearablePlay() {
            @Override
            public void Wear(WearableTreasureCard wearable, Character target) {
                System.out.printf("Надеваю %s\n", wearable.getName());
                ((ArmorWearableTreasureCard) wearable).wearArmor(target);
            }

            @Override
            public void UnWear(WearableTreasureCard wearable, Character target) {
                ((ArmorWearableTreasureCard) wearable).unWearArmor(target);
            }
        });
        cards.add(leather_armor);

        return cards;
    }
}
