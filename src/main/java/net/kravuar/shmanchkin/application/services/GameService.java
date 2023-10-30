package net.kravuar.shmanchkin.application.services;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class GameService {
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final SubscribableChannel gameListChannel = MessageChannels.publishSubscribe().getObject();

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
        return getCurrentUser()
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
                    return Mono.empty();
                });
    }

    public synchronized Flux<ServerSentEvent<EventDTO>> joinGame(String lobbyName) {
        return getCurrentUser()
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
        return getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var game = currentUser.getGame();
                    game.removePlayer(currentUser);
                    return Mono.empty();
                });
    }

    public Mono<Void> kickPlayer(String username) {
        return getCurrentUser()
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
                    return Mono.empty();
                });
    }

    public Mono<Void> startGame() {
        return getCurrentUser()
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
        return getCurrentUser()
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
        return getCurrentUser()
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
        return getCurrentUser()
                .flatMap(currentUser -> {
                    var game = currentUser.getGame();
                    if (game == null)
                        return Mono.error(new UserIsIdleException("Пользователь не в игре."));
                    return Mono.just(new LobbyDTO(game));
                });
    }

    public Mono<UserInfo> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Пользователь не авторизован.")))
                .map(securityContext -> (UserInfo) securityContext.getAuthentication().getPrincipal());
    }
}
