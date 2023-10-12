package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.UserInfoDTO;
import net.kravuar.shmanchkin.domain.model.game.UserInfo;

@Getter
public class PlayerEvent extends GameEvent {
    private final UserInfoDTO userInfo;

    public PlayerEvent(String lobbyName, UserInfo userInfo, String eventType) {
        super(lobbyName, eventType);
        this.userInfo = new UserInfoDTO(userInfo);
    }
}
