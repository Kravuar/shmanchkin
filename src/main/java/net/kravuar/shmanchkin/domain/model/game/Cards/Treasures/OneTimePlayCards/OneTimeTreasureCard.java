package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.OneTimePlayCards;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.TreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;

public class OneTimeTreasureCard extends TreasureCard {
    public OneTimeTreasureCard(String name, int price) {
        super(Type.ONE_TIME);
        setName(name);
        setPrice(price);
    }

    @Override
    public void Play(Selectable target) {
        play.Play(this, target);
    }

    @Override
    public void Leave(Selectable target) {
        play.Leave(this, target);
    }

    public void setPlay(OneTimePlay play) {
        this.play = play;
    }

    OneTimePlay play;
}
