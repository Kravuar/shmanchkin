package net.kravuar.shmanchkin.domain.model.game.Cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public Deck() {
        providers = new ArrayList<>();
    }

    public interface Provider {
        ArrayList<Card> GetCards();
    }

    public void initCards() {
        cards = new ArrayList<>();

        for (Provider deckProvider : providers) {
            cards.addAll(deckProvider.GetCards());
        }
        Collections.shuffle(cards);
    }

    public Card pullCard() {
        if (!cards.isEmpty()) {
            var card = cards.get(0);
            cards.remove(0);
            return card;
        } else
            return null;
    }

    public Boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addDeckProvider(Provider provider) {
        providers.add(provider);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    private final ArrayList<Provider> providers;
    private ArrayList<Card> cards;
}
