package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Curses;

import net.kravuar.shmanchkin.domain.model.game.Person.Person;

public interface CursePlay {
    void Play(CurseCard card, Person target);
    void Leave(CurseCard card, Person target);
}
