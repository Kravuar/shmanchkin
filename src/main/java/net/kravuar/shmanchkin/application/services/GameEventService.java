package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.events.LobbyListUpdateEvent;
import net.kravuar.shmanchkin.domain.model.events.LobbyStatusChangedEvent;
import net.kravuar.shmanchkin.domain.model.events.PlayerLobbyUpdateEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameEventService {
    private final ApplicationEventPublisher publisher;

    public void publishPlayerUpdate(GameLobby gameLobby, UserInfo player, LobbyPlayerUpdateAction action) {
        publisher.publishEvent(new PlayerLobbyUpdateEvent(
                gameLobby,
                player,
                action
        ));
    }

    public void publishLobbyStatusUpdate(GameLobby gameLobby, LobbyStatus status) {
        publisher.publishEvent(new LobbyStatusChangedEvent(
                gameLobby,
                status
        ));
    }

    public void publishLobbyListUpdate(GameLobby gameLobby, LobbyListUpdateAction action) {
        publisher.publishEvent(new LobbyListUpdateEvent(
                gameLobby,
                action
        ));
    }
}
