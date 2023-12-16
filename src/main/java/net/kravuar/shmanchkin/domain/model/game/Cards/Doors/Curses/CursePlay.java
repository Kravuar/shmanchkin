package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Curses;

import net.kravuar.shmanchkin.domain.model.game.character.Character;

public interface CursePlay {
    void Play(CurseCard card, Character target);
    void Leave(CurseCard card, Character target);
}
