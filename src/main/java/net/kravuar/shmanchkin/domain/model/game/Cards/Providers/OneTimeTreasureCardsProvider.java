package net.kravuar.shmanchkin.domain.model.game.Cards.Providers;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.OneTimePlayCards.OneTimePlay;
import net.kravuar.shmanchkin.domain.model.game.Cards.Treasures.OneTimePlayCards.OneTimeTreasureCard;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;

public class OneTimeTreasureCardsProvider implements Deck.Provider {
    public ArrayList<Card> GetCards() {

        ArrayList<Card> cards = new ArrayList<>();

        OneTimeTreasureCard magic_rocket = new OneTimeTreasureCard("Ракета магического назначения", 300);
        magic_rocket.setPlay(new OneTimePlay() {
            @Override
            public void Play(OneTimeTreasureCard card, Selectable target) {
                System.out.printf("Играю %s\n", card.getName());
                target.ChangeAdditionalPower(5);
            }

            @Override
            public void Leave(OneTimeTreasureCard card, Selectable target) {
                target.ChangeAdditionalPower(-5);
            }
        });
        cards.add(magic_rocket);

        OneTimeTreasureCard enlightenment = new OneTimeTreasureCard("Достигни просветления", 0);
        enlightenment.setPlay(new OneTimePlay() {
            @Override
            public void Play(OneTimeTreasureCard card, Selectable target) {
                System.out.printf("Играю %s\n", card.getName());
                ((Character) target).increaseLevel(1);
            }

            @Override
            public void Leave(OneTimeTreasureCard card, Selectable target) {
                ((Character) target).decreaseLevel(1);
            }
        });
        cards.add(enlightenment);

        return cards;
    }
}
