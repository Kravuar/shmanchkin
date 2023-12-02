package net.kravuar.shmanchkin.domain.model.game.Cards.Doors;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;

public abstract class DoorCard extends Card {
    public enum Type {
        CURSE,
        MONSTER,
        RACE,
        CLASS,
        BATTLE
    }

    public DoorCard(Type type) {
        super(ShirtType.DOOR);

        this.type = type;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type type;
}
