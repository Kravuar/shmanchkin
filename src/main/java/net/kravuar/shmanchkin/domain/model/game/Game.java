package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.exceptions.game.GameIsActiveException;
import net.kravuar.shmanchkin.domain.model.game.BattleField.BattleField;
import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Cards.Deck;
import net.kravuar.shmanchkin.domain.model.game.Cards.DeckManager;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.Monster;
import net.kravuar.shmanchkin.domain.model.game.Cards.Providers.MonsterProvider;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Game {
    private final Map<String, Character> characters = new LinkedHashMap<>();
    @Getter
    private Character currentTurnCharacter = null;
    @Getter
    private boolean isActive = false;

//
//

    private final Deck doorDeck = DeckManager.getDoorDeck();
    private final Deck treasureDeck = DeckManager.getTreasureDeck();
//    mda
    private final Deck.Provider monsters = new MonsterProvider();
    private BattleField battleField;


//
//

    public Character getCharacter(String name) {
        return characters.get(name);
    }
    public void addCharacter(String name, Character character) {
        if (isActive)
            throw new GameIsActiveException(this);
        characters.put(name, character);
    }
    public boolean removeCharacter(String name) {
//        TODO: handle mid game removal
        return characters.remove(name) != null;
    }

    public void start() {
        if (isActive)
            throw new GameIsActiveException(this);
        dealCards();
//        TODO: validate start
//        TODO: other initialization
        currentTurnCharacter = getNextCharacter();
        isActive = true;
    }

    public void endTurn() {
        System.out.print("Бой начался\n");
        StringBuilder names = new StringBuilder();
        for (var participant : battleField.MonsterSide.Participants) {
            var monster = (Monster)participant;
            names.append("Monster: ");
            names.append(monster.getName());
            names.append("(");
            names.append("Power: ");
            names.append(monster.GetPower().toString());
            names.append(") ");
        }
        System.out.printf("Сторона монстров: %s\n", names.toString());

        names = new StringBuilder();
        for (var person : battleField.PlayerSide.Participants) {
            names.append("Player");
            names.append("(");
            names.append("Power: ");
            names.append(person.GetPower().toString());
            names.append(") ");
        }
        System.out.printf("Сторона игроков: %s\n", names.toString());
        var playersWin = battleField.PlayerSide.GetPower() > battleField.MonsterSide.GetPower();
        System.out.println("Dobrota: " + battleField.PlayerSide.GetPower());
        System.out.println("Zlo: " + battleField.MonsterSide.GetPower());
        System.out.printf("Победила сторона %s\n", playersWin ? "игроков" : "монстров");
    }
    public boolean escapeBattle(Character character) {
//    TODO: return dice throw result or smth
        return false;
    }
    public void handleCard(Card card, Character character) {
        if (character.equals(currentTurnCharacter))
            System.out.println("Ne tvoi hod, nu da lando, hodi");
        System.out.println("PLAYED CARD");
        System.out.println("Name: " + card.getName());
        System.out.println("Description: " + card.getDescription());
        System.out.println(card);
        card.Play(character);
    }

    private Character getNextCharacter() {
        var charactersList = new ArrayList<>(characters.values());
        if (currentTurnCharacter == null)
            return charactersList.get(0);
        var nextIndex = charactersList.indexOf(currentTurnCharacter) % characters.size();
        return charactersList.get(nextIndex);
    }
    private void dealCards() {
        for (var character: characters.values()) {
            var a1 = doorDeck.pullCard();
            var b3 = treasureDeck.pullCard();

            System.out.println("Dostali door:");
            System.out.println("Name: " + a1.getName());
            System.out.println("Description : " + a1.getDescription());
            System.out.println("Dostali treasure:");
            System.out.println("Name: " + b3.getName());
            System.out.println("Description : " + b3.getDescription());

            character.cardsInHand.add(a1);
            character.cardsInHand.add(b3);
        }
        var eee5 = (Monster) monsters.GetCards().get(0);
        System.out.println("Monstra v bitvu: ");
        System.out.println("Name: " + eee5.getName());
        System.out.println("Description: " + eee5.getDescription());
        System.out.println("TreasureCount: " + eee5.getTreasuresCount());
        System.out.println("Power: " + eee5.GetPower());

        battleField = new BattleField();
        battleField.MonsterSide.Participants.add(eee5);
    }
}