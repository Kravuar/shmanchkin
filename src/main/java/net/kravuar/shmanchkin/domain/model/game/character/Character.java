package net.kravuar.shmanchkin.domain.model.game.character;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Common.Selectable;
import net.kravuar.shmanchkin.domain.model.game.Gear.ArmorGear;
import net.kravuar.shmanchkin.domain.model.game.Gear.WeaponGear;
import net.kravuar.shmanchkin.domain.model.game.Gear.WearableGear;

import java.util.ArrayList;

@Getter
public class Character implements Selectable {
    public enum Race {    //раса персонажа
        elf,            //эльф
        halfling,       //халфлинг
        dwarf,          //дварф
        half_breed,     //полукровка
        human           //человек
        ;
    }

    public enum Class {  //класс персонажа
        cleric,         //клирик
        wizard,         //волшебкик
        thief,          //вор
        warrior,        //воин
        none            //пусто
        ;
    }

    @Override
    public void Select() {
    }

    @Override
    public void ChangeAdditionalPower(int power_changes) {
        additional_power += power_changes;
    }

    public Race getRace() {
        return race;
    }

    public int getLevel() {
        return level;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Class getCur_class() {
        return cur_class;
    }

    public void setCur_class(Class cur_class) {
        this.cur_class = cur_class;
    }

    private void calculate_Total_Damage() { //высчитывает общий урон
        int temp = 0;
        temp += this.level;

        for (var w : this.weapons)
            temp += w.getPower();
        for (var o : this.others)
            temp += o.getPower();

        temp += helmet != null ? helmet.getPower() : 0;
        temp += body != null ? body.getPower() : 0;
        temp += legs != null ? legs.getPower() : 0;

        temp += this.additional_power;

        this.total_damage = temp;
    }

    public void decreaseLevel(int points) {
        // понизить уровень на заданный points, проверить чтобы левел не был ниже начального(1)
        if (level > 1) {
            --level;
        }
    }

    public void increaseLevel(int points) {
        // повысить уровень на заданный points, проверить чтобы левел не был выше 10
        level += points;
    }

    public void resetLevel() {
        // сбросить уровень до начальноого
    }

    @Override
    public Integer GetPower() {
        calculate_Total_Damage();
        return total_damage;
    }

    public ArrayList<Card> cardsInHand = new ArrayList<>();
    public ArrayList<WeaponGear> weapons = new ArrayList<>(); //оружие в руке
    public int hand_size = 0;
    public ArmorGear helmet;    //шлем
    public ArmorGear body;        //тело
    public ArmorGear legs;        //ноги
    public ArrayList<WearableGear> others = new ArrayList<>(); //другое снаряжение
    Race race = Race.human;          // текущая раса
    Class cur_class = Class.none;    // текущий класс
    int level = 1;          // текущий уровень
    private int total_damage;
    private int additional_power;
}
