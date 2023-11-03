package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.GameFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.LobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListFullUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.GameListUpdateDTO;
import net.kravuar.shmanchkin.domain.model.dto.events.MessageDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.*;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.GameListUpdateAction;
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
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> autoCloseTasks = new ConcurrentHashMap<>();
    private final SubscribableChannel gameListChannel = MessageChannels.publishSubscribe().getObject();

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

    private void scheduleAutoClose(String lobbyName) {
        var autoCloseTask = executorService.schedule(
                () -> autoClose(lobbyName),
                30,
                TimeUnit.SECONDS
        );
        autoCloseTasks.put(lobbyName, autoCloseTask);
    }

    private void cancelAutoClose(String lobbyName) {
        var task = autoCloseTasks.get(lobbyName);
        if (task != null)
            task.cancel(false);
    }

    public Flux<LobbyDTO> getLobbyList() {
        return Flux.fromIterable(games.values().stream()
                .map(LobbyDTO::new).toList());
    }

    public Map<String, Game> getGames() {
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
                        return Mono.error(new AlreadyInGameException(currentUser.getGame().getLobbyName()));
                    var game = new Game(
                            gameForm.getLobbyName(),
                            currentUser
                    );
                    var previousGame = games.putIfAbsent(
                            gameForm.getLobbyName(),
                            game
                    );
                    if (previousGame != null)
                        return Mono.error(new GameAlreadyExistsException(previousGame.getLobbyName()));
                    currentUser.setInfo(
                            game,
                            null,
                            null
                    );
                    gameListChannel.send(new GenericMessage<>(new GameListUpdateDTO(game, GameListUpdateAction.CREATED)));
                    gameListChannel.send(new GenericMessage<>(new GameListFullUpdateDTO(this.games.values())));
                    scheduleAutoClose(game.getLobbyName());
                    return Mono.empty();
                });
    }

    public synchronized Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName) {
        return userService.getCurrentUser()
                .flatMapMany(currentUser -> {
                    if (!currentUser.isIdle())
                        return Flux.error(new AlreadyInGameException(currentUser.getGame().getLobbyName()));

                    var game = games.get(lobbyName);
                    if (game == null)
                        return Flux.error(new GameNotFoundException(lobbyName));

                    return Flux.create(sink -> {
                        MessageHandler handler = message -> {
                            var gameEvent = (EventDTO) message.getPayload();
                            var event = ServerSentEvent.<EventDTO>builder()
                                    .event(gameEvent.getEventType())
                                    .data(gameEvent)
                                    .build();
                            sink.next(event);
                        };
                        try {
                            currentUser.setInfo(game, sink, handler);
                            game.addPlayer(currentUser);
                            cancelAutoClose(lobbyName);
                            sink.onDispose(() -> game.removePlayer(currentUser));
                        } catch (Exception joinFailedException) {
                            sink.error(joinFailedException);
                            sink.complete();
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
                    var game = currentUser.getGame();
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
                    var game = currentUser.getGame();
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
                    var game = currentUser.getGame();
                    if (!Objects.equals(game.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionException(game.getLobbyName(), "Начать игру"));
                    game.start();
                    return Mono.empty();
                });
    }

    public Mono<Void> closeGame() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.getGame() == null)
                        return Mono.error(new UserIsIdleException("Нет созданной игры."));
                    var game = currentUser.getGame();
                    if (!Objects.equals(game.getOwner(), currentUser))
                        return Mono.error(new ForbiddenActionException(game.getLobbyName(), "Закрыть игру"));
                    game.close();
                    games.remove(game.getLobbyName());
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
                        currentUser.getGame().send(new MessageDTO(currentUser, message));
                        return Mono.empty();
                    }
                });
    }

    public Mono<LobbyDTO> getLobbyInfo() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    var game = currentUser.getGame();
                    if (game == null)
                        return Mono.error(new UserIsIdleException("Пользователь не в игре."));
                    return Mono.just(new LobbyDTO(game));
                });
    }

    public UserInfo getActiveUser(@NonNull UUID uuid) {
        UserInfo user = null;
        for (var game: getGames().values()) {
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
}
