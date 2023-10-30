package net.kravuar.shmanchkin.domain.model.dto.events;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.PlayerDTO;

import java.time.ZonedDateTime;

@Getter
@Setter
public class MessageDTO extends EventDTO {
    private String message;
    private ZonedDateTime when;
    private PlayerDTO sender;

    public MessageDTO(UserInfo sender, String message) {
        super("player-message");
        this.message = message;
        this.when = ZonedDateTime.now();
        this.sender = new PlayerDTO(sender);
    }
}
