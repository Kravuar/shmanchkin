package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.props.GameProps;
import net.kravuar.shmanchkin.domain.model.account.GameSubscription;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.FullLobbyDTO;
import net.kravuar.shmanchkin.domain.model.dto.GameLobbyFormDTO;
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
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Validated
public class GameLobbyService {
    private final GameProps gameProps;
    private final UserService userService;
    private final GameEventService gameEventService;
    private final Map<String, GameLobby> lobbies = new ConcurrentHashMap<>();
    private final SubscribableChannel gameListChannel = MessageChannels
            .publishSubscribe("GameListChannel")
            .getObject();

    public Flux<FullLobbyDTO> getLobbyList() {
        return Flux.fromIterable(lobbies.values())
                .map(FullLobbyDTO::new);
    }
    public Map<String, GameLobby> getLobbies() {
        return Collections.unmodifiableMap(lobbies);
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

    public Mono<Void> createGameLobby(GameLobbyFormDTO gameLobbyForm) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (!currentUser.isIdle())
                        return Mono.error(new AlreadyInGameLobbyException(currentUser.getSubscription().getGameLobby()));
                    var gameLobby = new SubscribableGameLobby(
                        gameLobbyForm.getLobbyName(),
                        currentUser,
                        gameProps.getLobbyMinPlayers(),
                        gameProps.getLobbyMaxPlayers()
                    );
                    var previousGame = lobbies.putIfAbsent(
                            gameLobbyForm.getLobbyName(),
                            gameLobby
                    );
                    if (previousGame != null)
                        return Mono.error(new GameLobbyAlreadyExistsException(previousGame));

                    gameEventService.addGameLobby(gameLobby);
                    gameEventService.publishGameLobbyEvent(
                        new LobbyListUpdateEvent(
                            gameLobby,
                            LobbyListUpdateAction.CREATED
                        )
                    );
                    return Mono.empty();
                });
    }
    public Flux<ServerSentEvent<EventDTO>> joinGameLobby(String lobbyName) {
        return userService.getCurrentUser()
                .flatMapMany(currentUser -> {
                    if (!currentUser.isIdle())
                        return Flux.error(new AlreadyInGameLobbyException(currentUser.getSubscription().getGameLobby()));

                    var gameLobby = lobbies.get(lobbyName);
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
    public Mono<Void> leaveGameLobby() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    gameLobby.removePlayer(currentUser, false);
                    return Mono.empty();
                });
    }
    public Mono<Void> kickPlayerFromCurrentLobby(String username) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenLobbyActionException(gameLobby, "Выгнать игрока", "Вы не хост"));
                    var player = gameLobby.getPlayer(username);
                    if (player == null)
                        return Mono.error(new PlayerNotFoundException(gameLobby, username));
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
                        return Mono.error(new ForbiddenLobbyActionException(gameLobby, "Начать игру", "Вы не хост"));
                    gameLobby.start();
                    return Mono.empty();
                });
    }
    public Mono<Void> closeCurrentGameLobby() {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException("Нет созданной игры."));
                    var gameLobby = currentUser.getSubscription().getGameLobby();
                    if (!Objects.equals(gameLobby.getOwner(), currentUser))
                        return Mono.error(new ForbiddenLobbyActionException(gameLobby, "Закрыть игру", "Вы не хост"));
                    return close(gameLobby);
                });
    }
    public Mono<Void> close(GameLobby gameLobby) {
        lobbies.remove(gameLobby.getLobbyName());
        gameLobby.close();
        return Mono.empty();
    }

    public Mono<Void> sendMessage(String message) {
        return userService.getCurrentUser()
                .flatMap(currentUser -> {
                    if (currentUser.isIdle())
                        return Mono.error(new UserIsIdleException());
                    gameEventService.publishGameLobbyEvent(
                        new MessageEvent(
                            currentUser.getSubscription().getGameLobby(),
                            message,
                            currentUser
                        )
                    );
                    return Mono.empty();
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

//    Order matters
    @EventListener(PlayerLobbyUpdateEvent.class)
    protected void notify(PlayerLobbyUpdateEvent event) {
        var full = new LobbyPlayersFullUpdateDTO(event.getGameLobby().getPlayers());
        var single = new LobbyPlayersUpdateDTO(event.getPlayer(), event.getAction());

        for (var user: event.getGameLobby().getPlayers()) {
            user.send(single);
            user.send(full);
        }
    }
    @EventListener(MessageEvent.class)
    protected void notify(MessageEvent event) {
        var messageDTO = new MessageDTO(event);
        for (var user: event.getGameLobby().getPlayers())
            user.send(messageDTO);
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
        gameListChannel.send(new GenericMessage<>(new LobbyListFullUpdateDTO(this.lobbies.values())));
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
}
