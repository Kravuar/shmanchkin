package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards;

import net.kravuar.shmanchkin.domain.model.game.Gear.WeaponGear;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

public class WeaponWearableTreasureCard extends WearableTreasureCard {

    public WeaponWearableTreasureCard(String name, int price, WeaponGear gear) {
        super(name, price);
        this.gear = gear;
    }

    public boolean addWeapon(Character person) {
        if (person.hand_size + gear.getSize().getNum() > 2)
            return false;

        person.weapons.add(gear);
        person.hand_size += gear.getSize().getNum();
        return true;
    }

    public boolean removeWeapon(Character person) {
        person.weapons.remove(gear);
        person.hand_size -= gear.getSize().getNum();
        return true;
    }
    final private WeaponGear gear;
}
