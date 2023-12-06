package net.kravuar.shmanchkin.domain.model.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.character.Character;

@Getter
public class EscapeAttemptedEvent extends GameEvent {
    private final Character character;
    private final boolean escaped;

    public EscapeAttemptedEvent(Game game, Character character, boolean escaped) {
        super(game);
        this.character = character;
        this.escaped = escaped;
    }
}
