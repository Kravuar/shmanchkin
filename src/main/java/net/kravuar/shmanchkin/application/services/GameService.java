package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.GameProps;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListFullUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.MessageDTO;
import net.kravuar.shmanchkin.domain.model.events.LobbyCreatedEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.game.GameLobby;
import net.kravuar.shmanchkin.domain.model.game.GameListUpdateAction;
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
                .doOnSuccess(gameLobby -> publisher.publishEvent(new LobbyCreatedEvent(gameLobby)))
                .then();
    }

    public Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName) {
        return userService.getCurrentUser()
                .flatMapMany(currentUser -> {
                    if (!currentUser.isIdle())
                        return Flux.error(new AlreadyInGameException(currentUser.getSubscription().getGameLobby().getLobbyName()));

                    var game = games.get(lobbyName);
                    if (game == null)
                        return Flux.error(new GameNotFoundException(lobbyName));

                    return Flux.create(sink -> {
                        var handler = simpleSinkHandler(sink);
                        try {
                            game.addPlayer(currentUser);
                            sink.onDispose(() -> {
                                game.removePlayer(currentUser);
                                if (game.getPlayers().isEmpty())
                                    scheduleAutoClose(game.getLobbyName());
                            });
                        } catch (Exception joinFailedException) {
                            sink.error(joinFailedException);
                            sink.complete();
                        }
                    }, FluxSink.OverflowStrategy.LATEST);
                }).doOnSubscribe(() -> publisher.publishEvent());
    }

    public Mono<Void> leaveGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var game = currentUser.getGameLobby();
                    game.removePlayer(currentUser);
                    if (game.getPlayers().isEmpty())
                        scheduleAutoClose(game.getLobbyName());
                    return Mono.empty();
                });
    }

    public Mono<Void> kickPlayer(String username) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var game = currentUser.getGameLobby();
                    if (!Objects.equals(game.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionException(game.getLobbyName(), "Выгнать игрока"));
                    var player = game.getPlayer(username);
                    if (player == null)
                        return Mono.error(new PlayerNotFoundException(game.getLobbyName(), username));
                    game.removePlayer(player);
                    if (game.getPlayers().isEmpty())
                        scheduleAutoClose(game.getLobbyName());
                    return Mono.empty();
                });
    }

    public Mono<Void> startGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var game = currentUser.getGameLobby();
                    if (!Objects.equals(game.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionException(game.getLobbyName(), "Начать игру"));
                    game.start();
                    return Mono.empty();
                });
    }

    public Mono<Void> closeGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.getGameLobby() == null)
                        return Mono.error(new UserIsIdleException("Нет созданной игры."));
                    var game = currentUser.getGameLobby();
                    if (!Objects.equals(game.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionException(game.getLobbyName(), "Закрыть игру"));
                    games.remove(game.getLobbyName());
                    game.close();
                    gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, GameListUpdateAction.CLOSED)));
                    gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(games.values())));
                    return Mono.empty();
                });
    }

    public Mono<Void> sendMessage(String message) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    else {
                        currentUser.getGameLobby().send(new MessageDTO(currentUser, message));
                        return Mono.empty();
                    }
                });
    }

    public Mono<LobbyDTO> getLobbyInfo() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    var game = currentUser.getGameLobby();
                    if (game == null)
                        return Mono.error(new UserIsIdleException("Пользователь не в игре."));
                    return Mono.just(new LobbyDTO(game));
                });
    }

    public UserInfo getActiveUser(@NonNull UUID uuid) {
        UserInfo user = null;
        for (var game : getLobbies().values()) {
            if (game.getOwner().getUuid().equals(uuid)) {
                user = game.getOwner();
                break;
            }
            user = game.getPlayer(uuid);
            if (user != null)
                break;
        }
        return user;
    }

    private void autoClose(String lobbyName) {
        var game = games.get(lobbyName);
        if (game == null)
            return;
        if (!game.getPlayers().isEmpty())
            return;
        game.close();
        games.remove(lobbyName);
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, GameListUpdateAction.CLOSED)));
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

    @EventListener(LobbyCreatedEvent.class)
    private void sendNotification(LobbyCreatedEvent event) {
        gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(event.getGameLobby(), GameListUpdateAction.CREATED)));
        gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
    }

    @EventListener(LobbyCreatedEvent.class)
    private void scheduleAutoClose(LobbyCreatedEvent event) {
        scheduleAutoClose(event.getGameLobby());
    }
}
