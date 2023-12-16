package net.kravuar.shmanchkin.domain.model.game.Gear;

public class ArmorGear extends WearableGear{
    public enum Slot {
        Helmet(0),
        Body(1),
        Legs(2),

        Other(3);

        Slot(int slot) {
            this.slot_num = slot;
        }

        public int getNum() {
            return slot_num;
        }

        final private int slot_num;
    }

    public ArmorGear(int power, boolean is_big, Slot slot) {
        super(power, is_big);
        this.slot = slot;
    }

    public Slot getSlot() {
        return slot;
    }

    final private Slot slot;
}
