package net.kravuar.shmanchkin.domain.model.dto.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

@Getter
public class EscapeAttemptedDTO extends EventDTO {
    private final Character character;
    private final boolean escaped;

    public EscapeAttemptedDTO(Character character, boolean escaped) {
        super("escape-attempted");
        this.character = character;
        this.escaped = escaped;
    }
}
