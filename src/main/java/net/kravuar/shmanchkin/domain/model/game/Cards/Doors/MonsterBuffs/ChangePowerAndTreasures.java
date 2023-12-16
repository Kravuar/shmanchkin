package net.kravuar.shmanchkin.domain.model.game.Cards.Doors.MonsterBuffs;


import net.kravuar.shmanchkin.domain.model.game.Cards.Doors.Monster.Monster;

public class ChangePowerAndTreasures implements MonsterBuffPlay {

    public ChangePowerAndTreasures(int power_change, int treasure_change) {
        this.power_change = power_change;
        this.treasure_change = treasure_change;
    }

    @Override
    public void Play(MonsterBuff card, Monster target) {
        target.ChangeAdditionalPower(power_change);
        target.changeTreasureCount(treasure_change);
    }

    @Override
    public void Leave(MonsterBuff card, Monster target) {
        target.ChangeAdditionalPower(-power_change);
        target.changeTreasureCount(-treasure_change);
    }

    private final int power_change;
    private final int treasure_change;
}
