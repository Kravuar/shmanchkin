package net.kravuar.shmanchkin.domain.model.events.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.GameLobbyEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@Getter
public class GameLobbyAwareGameEvent<T extends GameEvent> extends GameLobbyEvent implements ResolvableTypeProvider {
    private final T gameEvent;

    public GameLobbyAwareGameEvent(GameLobby gameLobby, T gameEvent) {
        super(gameLobby);
        this.gameEvent = gameEvent;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(),
                ResolvableType.forInstance(this.gameEvent)
        );
    }
}
