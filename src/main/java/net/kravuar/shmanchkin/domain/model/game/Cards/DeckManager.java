package net.kravuar.shmanchkin.domain.model.game.Cards;

import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.*;

public class DeckManager {

    public static Deck getDoorDeck() {
        var door_deck = new Deck();
        door_deck.addDeckProvider(new MonsterProvider());
        door_deck.addDeckProvider(new CursesCardProvider());
        door_deck.addDeckProvider(new MonsterBuffsProvider());

        door_deck.initCards();
        return door_deck;
    }

    public static Deck getTreasureDeck() {
        var treasure_deck = new Deck();
        treasure_deck.addDeckProvider(new WearableTreasureCardsProvider());
        treasure_deck.addDeckProvider(new OneTimeTreasureCardsProvider());

        treasure_deck.initCards();
        return treasure_deck;
    }

}