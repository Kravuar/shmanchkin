package net.kravuar.shmanchkin.domain.model.game.Cards.Providers;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.MonsterBuffs.ChangePowerAndTreasures;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.MonsterBuffs.MonsterBuff;

import java.util.ArrayList;

public class MonsterBuffsProvider implements Deck.Provider {

    @Override
    public ArrayList<Card> GetCards() {
        var cards = new ArrayList<Card>();

        var big = new MonsterBuff("Громадный 0_0");
        big.setPlay(new ChangePowerAndTreasures(10, 2));
        cards.add(big);

        var smart = new MonsterBuff("Умный");
        smart.setPlay(new ChangePowerAndTreasures(5, 1));
        cards.add(smart);

        var smoll = new MonsterBuff("Грудничок");
        smoll.setPlay(new ChangePowerAndTreasures(-5, -1));
        cards.add(smoll);

        return cards;
    }
}
