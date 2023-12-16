package net.kravuar.shmanchkin.domain.model.game.Common;

public interface Selectable {
    public abstract void Select();
    public abstract void ChangeAdditionalPower(int power_changes);
    public abstract Integer GetPower();
}
