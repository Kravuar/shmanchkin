package net.kravuar.shmanchkin.domain.model.game.Cards.Treasures;

import net.kravuar.shmanchkin.domain.model.game.Cards.Card;

public abstract class TreasureCard extends Card {
    public enum Type {
        ONE_TIME, // шмотка
        WEARABLE // шмотка
    }

    public TreasureCard(Type type) {
        super(ShirtType.TREASURE);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    private Type type;
    private Integer price;
}
