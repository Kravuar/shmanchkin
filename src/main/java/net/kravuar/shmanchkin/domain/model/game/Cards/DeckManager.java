package net.kravuar.shmanchkin.domain.model.game.Cards;

import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.MonsterProvider;

public class DeckManager {
    public DeckManager() {
        door_deck = new Deck();
        door_deck.addDeckProvider(new MonsterProvider());
        door_deck.initCards();

        treasure_deck = new Deck();
        treasure_deck.initCards();
    }

    public Deck getDoorDeck() {
        return door_deck;
    }

    public Deck getTreasureDeck() {
        return treasure_deck;
    }

    private final Deck door_deck;
    private final Deck treasure_deck;
}
