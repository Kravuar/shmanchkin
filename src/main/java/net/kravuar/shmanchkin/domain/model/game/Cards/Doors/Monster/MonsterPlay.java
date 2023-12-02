package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster;

import net.kravuar.shmanchkin.domain.model.game.Person.Person;

public interface MonsterPlay {
    boolean Condition(Person enemy);
    void Play(Monster monster, Person enemy);
    void Reverse(Monster monster, Person enemy);
}