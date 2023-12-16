package net.kravuar.shmanchkin.domain.model.game.Gear;

public class WearableGear {
    WearableGear(int power, boolean isBig){
        this.power = power;
        this.isBig = isBig;
    }
    public int getPower() {
        return power;
    }

    public boolean isBig() {
        return isBig;
    }
    private final int power;

    private final boolean isBig;
}
