package net.kravuar.shmanchkin.domain.model.game.cards.door;

public interface MonsterBonus extends Door {
    int getBonus();

    boolean isApplicable(Monster monster);
}
