package net.kravuar.shmanchkin.domain.model.game.Cards;

import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;

public abstract class Card {
    public enum ShirtType {
        DOOR,
        TREASURE
    }

    public Card(ShirtType type) {
        shirtType = type;
    }
    public abstract void Play(Selectable target);
    public abstract void Leave(Selectable target);

    public ShirtType getShirtType() {
        return shirtType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String name;
    private String picture;
    private String description;
    private final ShirtType shirtType;
}
