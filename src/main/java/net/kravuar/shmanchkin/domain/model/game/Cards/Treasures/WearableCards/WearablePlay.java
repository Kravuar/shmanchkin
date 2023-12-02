package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards;

import net.kravuar.shmanchkin.domain.model.game.Person.Person;
public interface WearablePlay {
    void Wear(WearableTreasureCard wearable, Person target);
    void UnWear(WearableTreasureCard wearable, Person target);
}
