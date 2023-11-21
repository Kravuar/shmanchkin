package net.kravuar.shmanchkin.domain.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;

@RequiredArgsConstructor
@Getter
public class LobbyCreatedEvent {
    private final GameLobby gameLobby;
}
