package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.OneTimePlayCards;

import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;

public interface OneTimePlay {
    void Play(OneTimeTreasureCard card, Selectable target);
    void Leave(OneTimeTreasureCard card, Selectable target);
}
