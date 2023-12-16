package net.kravuar.shmanchkin.domain.model.game.Cards;

import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.CursesCardProvider;
import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.MonsterBuffsProvider;
import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.MonsterProvider;

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
        treasure_deck.initCards();
        return treasure_deck;
    }

}