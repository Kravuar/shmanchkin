package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster;

import net.kravuar.shmanchkin.domain.model.game.character.Character;

public interface MonsterPlay {
    boolean Condition(Character enemy);
    void Play(Monster monster, Character enemy);
    void Reverse(Monster monster, Character enemy);
}