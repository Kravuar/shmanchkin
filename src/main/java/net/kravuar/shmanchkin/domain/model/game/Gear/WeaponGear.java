package net.kravuar.shmanchkin.domain.model.game.Gear;

public class WeaponGear extends WearableGear{
    public enum Size {
        OneHand(1),
        TwoHand(2);

        Size(int slot) {
            this.slot = slot;
        }

        public int getNum() {
            return slot;
        }

        final private int slot;

    }
    public WeaponGear(int power, boolean isBig, Size size) {
        super(power, isBig);
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    private final Size size;
}
