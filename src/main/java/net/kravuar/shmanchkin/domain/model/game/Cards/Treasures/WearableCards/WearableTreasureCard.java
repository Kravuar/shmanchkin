package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.WearableCards;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.TreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;
import net.kravuar.shmanchkin.domain.model.game.Person.Person;

public class WearableTreasureCard extends TreasureCard {
    public WearableTreasureCard(String name, int price) {
        super(Type.WEARABLE);
        setName(name);
        setPrice(price);
    }

    @Override
    public void Play(Selectable target) {
        play.Wear(this, (Person) target);
    }

    @Override
    public void Leave(Selectable target) {
        play.UnWear(this, (Person) target);
    }

    public void setPlay(WearablePlay play) {
        this.play = play;
    }

    WearablePlay play = null;
}
