package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.GameProps;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.LobbyListUpdateEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.PlayerLobbyUpdateEvent;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.*;

@RequiredArgsConstructor
public class GameAutoCloseService {
    private final GameProps gameProps;
    private final GameLobbyService gameLobbyService;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<GameLobby, ScheduledFuture<?>> autoCloseTasks = new ConcurrentHashMap<>();

    public void scheduleAutoClose(GameLobby gameLobby) {
        var autoCloseTask = executorService.schedule(
                () -> {
                    gameLobbyService.close(gameLobby).block();
                    autoCloseTasks.remove(gameLobby);
                },
                gameProps.getAutoCloseTimeout(),
                TimeUnit.SECONDS
        );
        autoCloseTasks.put(gameLobby, autoCloseTask);
    }
    public void cancelAutoClose(GameLobby gameLobby) {
        var task = autoCloseTasks.get(gameLobby);
        if (task != null)
            task.cancel(false);
    }

    @EventListener(value = PlayerLobbyUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).CONNECTED")
    protected void cancelAutoClose(PlayerLobbyUpdateEvent event) {
        cancelAutoClose(event.getGameLobby());
    }
    @EventListener(value = PlayerLobbyUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).DISCONNECTED")
    protected void scheduleAutoCloseOnEmpty(PlayerLobbyUpdateEvent event) {
        var gameLobby = event.getGameLobby();
        if (gameLobby.getPlayers().isEmpty())
            scheduleAutoClose(gameLobby);
    }
    @EventListener(value = LobbyListUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction).CREATED")
    protected void scheduleAutoClose(LobbyListUpdateEvent event) {
        scheduleAutoClose(event.getGameLobby());
    }
}
