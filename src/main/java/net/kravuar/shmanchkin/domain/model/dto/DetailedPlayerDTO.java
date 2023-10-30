package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;

@Getter
@Setter
public class DetailedPlayerDTO extends PlayerDTO {
//    TODO: CharacterDTO field with stuff like level, hand, armor and so on.

    public DetailedPlayerDTO(UserInfo userInfo) {
        super(userInfo);
    }
}
