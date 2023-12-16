package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

public interface WearablePlay {
    void Wear(WearableTreasureCard wearable, Character target);
    void UnWear(WearableTreasureCard wearable, Character target);
}
