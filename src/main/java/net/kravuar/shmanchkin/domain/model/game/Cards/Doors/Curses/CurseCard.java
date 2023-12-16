package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Curses;

import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.DoorCard;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

public class CurseCard extends DoorCard {
    public CurseCard(String name) {
        super(Type.CURSE);
        setName(name);
    }

    @Override
    public void Play(Selectable target) {
        if (target instanceof Character) {
            play.Play(this, (Character) target);
        } else {
            System.out.println("Target not a person");
        }
    }

    @Override
    public void Leave(Selectable target) {
        if (target instanceof Character) {
            play.Leave(this, (Character) target);
        } else {
            System.out.println("Target not a person");
        }
    }

    public void setPlay(CursePlay play) {
        this.play = play;
    }

    private CursePlay play;
}
