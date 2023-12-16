package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.MonsterBuffs;

import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.DoorCard;
import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.Monster;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;

public class MonsterBuff extends DoorCard {
    public MonsterBuff(String name) {
        super(Type.BATTLE);
        setName(name);
    }

    @Override
    public void Play(Selectable target) {
        System.out.printf("Play %s\n", this.getName());
        if (target instanceof Monster) {
            play.Play(this, (Monster) target);
        } else {
            System.out.println("Target not a monster");
        }
    }

    @Override
    public void Leave(Selectable target) {
        System.out.printf("Leave %s\n", this.getName());
        if (target instanceof Monster) {
            play.Leave(this, (Monster) target);
        } else {
            System.out.println("Target not a monster");
        }
    }

    public void setPlay(MonsterBuffPlay play) {
        this.play = play;
    }

    private MonsterBuffPlay play;
}
