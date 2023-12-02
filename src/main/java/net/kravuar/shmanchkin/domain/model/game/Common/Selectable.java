package net.kravuar.shmanchkin.domain.model.game.Common;

public interface Selectable {
    void Select();
    void ChangeAdditionalPower(int power_changes);
    Integer GetPower();
}
