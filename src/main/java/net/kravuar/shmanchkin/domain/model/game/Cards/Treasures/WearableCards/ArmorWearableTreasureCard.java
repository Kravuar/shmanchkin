package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards;

import net.kravuar.shmanchkin.domain.model.game.Gear.ArmorGear;
import net.kravuar.shmanchkin.domain.model.game.Person.Person;

public class ArmorWearableTreasureCard extends WearableTreasureCard {
    public ArmorWearableTreasureCard(String name, int price, ArmorGear gear) {
        super(name, price);
        this.gear = gear;
    }

    public boolean wearArmor(Person target) {
        switch (gear.getSlot()) {
            case Helmet -> {
                if (target.helmet != null) {
                    return false;
                } else
                    target.helmet = gear;
            }
            case Body -> {
                if (target.body != null) {
                    return false;
                } else
                    target.body = gear;
            }
            case Legs -> {
                if (target.legs != null) {
                    return false;
                } else
                    target.legs =gear;
            }
            case Other -> target.others.add(gear);
        }
        return true;
    }

    public boolean unWearArmor(Person target) {
        switch (gear.getSlot()) {
            case Helmet -> target.helmet = null;
            case Body -> target.body = null;
            case Legs -> target.legs = null;
            case Other -> target.others.remove(gear);
        }
        return true;
    }
    final private ArmorGear gear;
}
