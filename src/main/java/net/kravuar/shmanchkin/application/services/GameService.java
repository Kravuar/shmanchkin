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
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.game.LobbyListUpdateAction;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
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
public class GameService {
    private final UserService userService;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<String, GameLobby> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> autoCloseTasks = new ConcurrentHashMap<>();
    private final SubscribableChannel gameListChannel = MessageChannels.publishSubscribe("GameList").getObject();
    private final ApplicationEventPublisher publisher;
    private final GameProps gameProps;

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
                            currentUser
                    );
                    var previousGame = games.putIfAbsent(
                            gameForm.getLobbyName(),
                            game
                    );
                    if (previousGame != null)
                        return Mono.error(new GameAlreadyExistsException(previousGame.getLobbyName()));
                    return Mono.just(game);
                })
                .doOnSuccess(gameLobby -> publisher.publishEvent(new LobbyListUpdateEvent(gameLobby, LobbyListUpdateAction.CREATED)))
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

                    return Flux.<ServerSentEvent<EventDTO>>create(sink -> {
                        try {
                            currentUser.setSubscription(new GameSubscription(
                                    gameLobby,
                                    sink,
                                    simpleSinkHandler(sink)
                            ));
                            gameLobby.addPlayer(currentUser);
                            sink.onDispose(() -> removePlayer(gameLobby, currentUser));
                        } catch (Exception joinFailedException) {
                            sink.error(joinFailedException);
                            currentUser.toIdle();
                        }
                    }, FluxSink.OverflowStrategy.LATEST);
                }).doOnSubscribe(() -> publisher.publishEvent()); // TODO: user joined + cancel autoclose handler
    }

    public Mono<Void> leaveGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    return removePlayer(gameLobby, currentUser);
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
                    return removePlayer(gameLobby, player);
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
                    return Mono.just(gameLobby);
                })
                .doOnSuccess(gameLobby -> publisher.publishEvent(new LobbyListUpdateEvent(gameLobby, LobbyListUpdateAction.CLOSED)))
                .then();
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

    private void autoClose(String lobbyName) {
        var gameLobby = games.get(lobbyName);
        if (gameLobby == null)
            return;
        if (!gameLobby.getPlayers().isEmpty())
            return;
        gameLobby.close();
        games.remove(lobbyName);
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(gameLobby, LobbyListUpdateAction.CLOSED)));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(games.values())));
        autoCloseTasks.remove(lobbyName);
    }

    private void scheduleAutoClose(GameLobby gameLobby) {
        var autoCloseTask = executorService.schedule(
                () -> autoClose(gameLobby.getLobbyName()),
                gameProps.getAutoCloseTimeout(),
                TimeUnit.SECONDS
        );
        autoCloseTasks.put(gameLobby.getLobbyName(), autoCloseTask);
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

    private Mono<Void> removePlayer(GameLobby lobby, UserInfo player) {
        lobby.removePlayer(player);
        if (lobby.getPlayers().isEmpty())
            scheduleAutoClose(lobby);
        return Mono.empty();
    }

    @EventListener(LobbyListUpdateEvent.class)
    private void sendNotification(LobbyListUpdateEvent event) {
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(event.getGameLobby(), event.getAction())));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
    }

    @EventListener(value = LobbyListUpdateEvent.class, condition = "#event.action == LobbyListUpdateAction.CREATED")
    private void scheduleAutoClose(LobbyListUpdateEvent event) {
        scheduleAutoClose(event.getGameLobby());
    }
}
