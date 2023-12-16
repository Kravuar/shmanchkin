package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.account.UserPrincipal;
import net.kravuar.shmanchkin.domain.model.game.Cards.Card;
import net.kravuar.shmanchkin.domain.model.game.Gear.ArmorGear;
import net.kravuar.shmanchkin.domain.model.game.Gear.WeaponGear;
import net.kravuar.shmanchkin.domain.model.game.Gear.WearableGear;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Data
public class CharacterDTO {
    private final ArrayList<Card> cardsInHand;
    private final int level;
    private final int total_damage;
    private final int additional_power;

    public CharacterDTO(Character character) {
        this.cardsInHand = (ArrayList<Card>) character.getCardsInHand().clone();
        this.level = character.getLevel();
        this.total_damage = character.getTotal_damage();
        this.additional_power = character.getAdditional_power();
    }
}
