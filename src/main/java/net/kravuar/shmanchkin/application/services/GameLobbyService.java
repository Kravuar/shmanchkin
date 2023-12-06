package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.GameProps;
import net.kravuar.shmanchkin.domain.model.account.GameSubscription;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.FullLobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.gameLobby.*;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.LobbyListUpdateEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.LobbyStatusChangedEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.MessageEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.PlayerLobbyUpdateEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.gameLobby.*;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;
import net.kravuar.shmanchkin.domain.model.gameLobby.SubscribableGameLobby;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Service
@Validated
public class GameLobbyService {
    private final UserService userService;
    private final GameProps gameProps;
    private final GameEventService gameEventService;
    private final Map<String, GameLobby> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> autoCloseTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final SubscribableChannel gameListChannel = MessageChannels
            .publishSubscribe("GameListChannel")
            .getObject();

    public Flux<FullLobbyDTO> getLobbyList() {
        return Flux.fromIterable(games.values())
                .map(FullLobbyDTO::new);
    }
    public Map<String, GameLobby> getLobbies() {
        return Collections.unmodifiableMap(games);
    }
    public Flux<ServerSentEvent<EventDTO>> subscribeToLobbyListUpdates() {
        return Flux.create(sink -> {
            MessageHandler handler = message -> {
                var gameEvent = (EventDTO) message.getPayload();
                var event = ServerSentEvent.<EventDTO>builder()
                        .event(gameEvent.getEventType())
                        .data(gameEvent)
                        .build();
                sink.next(event);
            };
            sink.onDispose(() -> gameListChannel.unsubscribe(handler));
            gameListChannel.subscribe(handler);
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public Mono<Void> createGame(GameFormDTO gameForm) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (!currentUser.isIdle())
                        return Mono.error(new AlreadyInGameLobbyException(currentUser.getSubscription().getGameLobby().getLobbyName()));
                    var game = new SubscribableGameLobby(
                        gameForm.getLobbyName(),
                        currentUser,
                        gameProps.getLobbyMinPlayers(),
                        gameProps.getLobbyMaxPlayers()
                    );
                    var previousGame = games.putIfAbsent(
                            gameForm.getLobbyName(),
                            game
                    );
                    if (previousGame != null)
                        return Mono.error(new GameLobbyAlreadyExistsException(previousGame.getLobbyName()));

                    gameEventService.addGameLobby(game);
                    gameEventService.publishGameLobbyEvent(
                        new LobbyListUpdateEvent(
                            game,
                            LobbyListUpdateAction.CREATED
                        )
                    );
                    return Mono.empty();
                });
    }
    public Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName) {
        return userService.getCurrentUser()
                .flatMapMany(currentUser -> {
                    if (!currentUser.isIdle())
                        return Flux.error(new AlreadyInGameLobbyException(currentUser.getSubscription().getGameLobby().getLobbyName()));

                    var gameLobby = games.get(lobbyName);
                    if (gameLobby == null)
                        return Flux.error(new GameLobbyNotFoundException(lobbyName));

                    return Flux.create(sink -> {
                        try {
                            currentUser.setSubscription(new GameSubscription(
                                    gameLobby,
                                    sink
                            ));
                            gameLobby.addPlayer(currentUser);
                            sink.onDispose(() -> gameLobby.removePlayer(currentUser, false));
                        } catch (Exception joinFailedException) {
                            sink.error(joinFailedException);
                            currentUser.toIdle();
                        }
                    }, FluxSink.OverflowStrategy.LATEST);
                });
    }
    public Mono<Void> leaveGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    gameLobby.removePlayer(currentUser, false);
                    return Mono.empty();
                });
    }
    public Mono<Void> kickPlayer(String username) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionLobbyException(gameLobby.getLobbyName(), "Выгнать игрока"));
                    var player = gameLobby.getPlayer(username);
                    if (player == null)
                        return Mono.error(new PlayerNotFoundException(gameLobby.getLobbyName(), username));
                    gameLobby.removePlayer(player, true);
                    return Mono.empty();
                });
    }

    public Mono<Void> startGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionLobbyException(gameLobby.getLobbyName(), "Начать игру"));
                    gameLobby.start();
                    return Mono.empty();
                });
    }
    public Mono<Void> closeGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException("Нет созданной игры."));
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionLobbyException(gameLobby.getLobbyName(), "Закрыть игру"));
                    games.remove(gameLobby.getLobbyName());
                    gameLobby.close();
                    return Mono.empty();
                });
    }

    public Mono<Void> sendMessage(String message) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    else {
                        gameEventService.publishGameLobbyEvent(
                            new MessageEvent(
                                currentUser.getSubscription().getGameLobby(),
                                message,
                                currentUser
                            )
                        );
                        return Mono.empty();
                    }
                });
    }

    public Mono<FullLobbyDTO> getLobbyInfo() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException("Пользователь не в игре."));
                    return Mono.just(new FullLobbyDTO(currentUser.getSubscription().getGameLobby()));
                });
    }
    public UserInfo getActiveUser(@NonNull UUID uuid) {
        UserInfo user = null;
        for (var lobby : getLobbies().values()) {
            if (lobby.getOwner().getUuid().equals(uuid)) {
                user = lobby.getOwner();
                break;
            }
            user = lobby.getPlayer(uuid);
            if (user != null)
                break;
        }
        return user;
    }

    private void scheduleAutoClose(GameLobby gameLobby) {
        var lobbyName = gameLobby.getLobbyName();
        var autoCloseTask = executorService.schedule(
                () -> {
                    games.remove(lobbyName);
                    gameLobby.close();
                    autoCloseTasks.remove(lobbyName);
                },
                gameProps.getAutoCloseTimeout(),
                TimeUnit.SECONDS
        );
        autoCloseTasks.put(lobbyName, autoCloseTask);
    }
    private void cancelAutoClose(GameLobby gameLobby) {
        var task = autoCloseTasks.get(gameLobby.getLobbyName());
        if (task != null)
            task.cancel(false);
    }

//    Order matters
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
    @EventListener(PlayerLobbyUpdateEvent.class)
    protected void notify(PlayerLobbyUpdateEvent event) {
        var full = new LobbyPlayersFullUpdateDTO(event.getGameLobby().getPlayers());
        var single = new LobbyPlayersUpdateDTO(event.getPlayer(), event.getAction());

        for (var user: event.getGameLobby().getPlayers()) {
            user.send(single);
            user.send(full);
        }
    }
    @EventListener(value = PlayerLobbyUpdateEvent.class, condition =
            "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).DISCONNECTED " +
            "|| #event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).KICKED"
    )
    protected void cancelSubscription(PlayerLobbyUpdateEvent event) {
        event.getPlayer().toIdle();
    }

    @EventListener(LobbyListUpdateEvent.class)
    protected void notifyLobbyListUpdate(LobbyListUpdateEvent event) {
        gameListChannel.send(new GenericMessage<>(new LobbyListUpdateDTO(event.getGameLobby(), event.getAction())));
        gameListChannel.send(new GenericMessage<>(new LobbyListFullUpdateDTO(this.games.values())));
    }
    @EventListener(value = LobbyStatusChangedEvent.class, condition = "#event.lobbyStatus == T(net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby$LobbyStatus).CLOSED")
    protected void removeLobbyListener(LobbyStatusChangedEvent event) {
        gameEventService.removeGameLobby((SubscribableGameLobby) event.getGameLobby());
    }
    @EventListener(LobbyStatusChangedEvent.class)
    protected void notifyLobbyStatusUpdate(LobbyStatusChangedEvent event) {
        for (var player: event.getGameLobby().getPlayers())
            player.send(new LobbyStatusChangedDTO(event.getLobbyStatus()));
//        Status change to IDLE wont happen
        notifyLobbyListUpdate(new LobbyListUpdateEvent(
                event.getGameLobby(),
                event.getLobbyStatus() == GameLobby.LobbyStatus.ACTIVE
                    ? LobbyListUpdateAction.STARTED
                    : LobbyListUpdateAction.CLOSED
        ));
    }
    @EventListener(value = LobbyListUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction).CREATED")
    protected void scheduleAutoClose(LobbyListUpdateEvent event) {
        scheduleAutoClose(event.getGameLobby());
    }
}
