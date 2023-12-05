package net.kravuar.shmanchkin.domain.model.dto.events.gameLobby;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.MessageEvent;

import java.time.ZonedDateTime;

@Getter
@Setter
public class MessageDTO extends EventDTO {
    private String message;
    private ZonedDateTime when;
    private UserDTO sender;

    public MessageDTO(MessageEvent messageEvent) {
        super("player-message");
        this.when = messageEvent.getTimestamp();
        this.message = messageEvent.getMessage();
        this.sender = new UserDTO(messageEvent.getSender());
    }
}
