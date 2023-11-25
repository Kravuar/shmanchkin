package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.GameProps;
import net.kravuar.shmanchkin.domain.model.account.GameSubscription;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListFullUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.MessageDTO;
import net.kravuar.shmanchkin.domain.model.events.LobbyListUpdateEvent;
import net.kravuar.shmanchkin.domain.model.events.LobbyStatusChangedEvent;
import net.kravuar.shmanchkin.domain.model.events.PlayerLobbyUpdateEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.gameLobby.GameLobby;
import net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction;
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
public class GameService {
    private final UserService userService;
    private final GameProps gameProps;
    private final GameEventService gameEventService;
    private final Map<String, GameLobby> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> autoCloseTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final SubscribableChannel gameListChannel = MessageChannels.publishSubscribe("GameListChannel").getObject();

    public Flux<LobbyDTO> getLobbyList() {
        return Flux.fromIterable(games.values())
                .map(LobbyDTO::new);
    }

    public Map<String, GameLobby> getLobbies() {
        return Collections.unmodifiableMap(games);
    }

    public Flux<ServerSentEvent<EventDTO>> subscribeToLobbyListUpdates() {
        return Flux.create(sink -> {
            var handler = simpleSinkHandler(sink);
            sink.onDispose(() -> gameListChannel.unsubscribe(handler));
            gameListChannel.subscribe(handler);
        }, FluxSink.OverflowStrategy.LATEST);
    }

    public Mono<Void> createGame(GameFormDTO gameForm) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (!currentUser.isIdle())
                        return Mono.error(new AlreadyInGameException(currentUser.getSubscription().getGameLobby().getLobbyName()));
                    var game = new GameLobby(
                            gameForm.getLobbyName(),
                            currentUser,
                            gameEventService,
                            gameProps.getLobbyMinPlayers(),
                            gameProps.getLobbyMaxPlayers()
                    );
                    var previousGame = games.putIfAbsent(
                            gameForm.getLobbyName(),
                            game
                    );
                    if (previousGame != null)
                        return Mono.error(new GameAlreadyExistsException(previousGame.getLobbyName()));
                    return Mono.just(game);
                })
                .doOnSuccess(gameLobby -> gameEventService
                        .publishLobbyListUpdate(
                                gameLobby,
                                LobbyListUpdateAction.CREATED
                        )
                )
                .then();
    }

    public Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName) {
        return userService.getCurrentUser()
                .flatMapMany(currentUser -> {
                    if (!currentUser.isIdle())
                        return Flux.error(new AlreadyInGameException(currentUser.getSubscription().getGameLobby().getLobbyName()));

                    var gameLobby = games.get(lobbyName);
                    if (gameLobby == null)
                        return Flux.error(new GameNotFoundException(lobbyName));

                    return Flux.create(sink -> {
                                try {
                                    currentUser.setSubscription(new GameSubscription(
                                            gameLobby,
                                            sink,
                                            simpleSinkHandler(sink)
                                    ));
                                    gameLobby.addPlayer(currentUser);
                                    sink.onDispose(() -> gameLobby.removePlayer(currentUser));
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
                    gameLobby.removePlayer(currentUser);
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
                        return Mono.error(new ForbiddenActionException(gameLobby.getLobbyName(), "Выгнать игрока"));
                    var player = gameLobby.getPlayer(username);
                    if (player == null)
                        return Mono.error(new PlayerNotFoundException(gameLobby.getLobbyName(), username));
                    gameLobby.kickPlayer(player);
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
                        return Mono.error(new ForbiddenActionException(gameLobby.getLobbyName(), "Начать игру"));
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
                        return Mono.error(new ForbiddenActionException(gameLobby.getLobbyName(), "Закрыть игру"));
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
                        currentUser.getSubscription().getGameLobby()
                                .send(new MessageDTO(currentUser, message));
                        return Mono.empty();
                    }
                });
    }

    public Mono<LobbyDTO> getLobbyInfo() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException("Пользователь не в игре."));
                    return Mono.just(new LobbyDTO(currentUser.getSubscription().getGameLobby()));
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

    private MessageHandler simpleSinkHandler(FluxSink<ServerSentEvent<EventDTO>> sink) {
        return message -> {
            var gameEvent = (EventDTO) message.getPayload();
            var event = ServerSentEvent.<EventDTO>builder()
                    .event(gameEvent.getEventType())
                    .data(gameEvent)
                    .build();
            sink.next(event);
        };
    }

    @EventListener(value = PlayerLobbyUpdateEvent.class,
            condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).CONNECTED"
    )
    protected void cancelAutoClose(PlayerLobbyUpdateEvent event) {
        cancelAutoClose(event.getGameLobby());
    }

    @EventListener(value = PlayerLobbyUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyPlayerUpdateAction).DISCONNECTED")
    protected void scheduleAutoCloseOnEmpty(PlayerLobbyUpdateEvent event) {
        var gameLobby = event.getGameLobby();
        if (gameLobby.getPlayers().isEmpty())
            scheduleAutoClose(gameLobby);
    }

    @EventListener(LobbyListUpdateEvent.class)
    protected void sendGameListUpdateNotification(LobbyListUpdateEvent event) {
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(event.getGameLobby(), event.getAction())));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
    }

    @EventListener(value = LobbyStatusChangedEvent.class, condition = "#event.status == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyStatus).CLOSED")
    protected void sendGameListUpdateNotification(LobbyStatusChangedEvent event) {
        sendGameListUpdateNotification(new LobbyListUpdateEvent(
                event.getGameLobby(),
                LobbyListUpdateAction.CLOSED
        ));
    }

    @EventListener(value = LobbyListUpdateEvent.class, condition = "#event.action == T(net.kravuar.shmanchkin.domain.model.gameLobby.LobbyListUpdateAction).CREATED")
    protected void scheduleAutoClose(LobbyListUpdateEvent event) {
        scheduleAutoClose(event.getGameLobby());
    }
}
