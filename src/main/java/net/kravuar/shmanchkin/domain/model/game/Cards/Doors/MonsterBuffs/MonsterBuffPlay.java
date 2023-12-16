package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.MonsterBuffs;

import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.Monster;

public interface MonsterBuffPlay {
    void Play(MonsterBuff card, Monster target);
    void Leave(MonsterBuff card, Monster target);
}

