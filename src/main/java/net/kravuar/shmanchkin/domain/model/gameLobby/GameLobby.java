package net.kravuar.shmanchkin.domain.model.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.application.services.GameEventService;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.events.*;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.IllegalLobbyStatusException;
import net.kravuar.shmanchkin.domain.model.exceptions.NotEnoughPlayersException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.character.CharacterImpl;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.*;

public class GameLobby {
    public final int minPlayers;
    public final int maxPlayers;

    private final GameEventService gameEventService;
    private final Game game;
    @Getter
    private final String lobbyName;
    @Getter
    private final UserInfo owner;
    @Getter
    private LobbyStatus lobbyStatus = LobbyStatus.IDLE;
    private final SubscribableChannel channel;
    private final Map<UUID, UserInfo> playersJoined = new HashMap<>();

    public GameLobby(String lobbyName, UserInfo owner, GameEventService gameEventService, int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.lobbyName = lobbyName;
        this.owner = owner;
        this.gameEventService = gameEventService;
        this.channel = MessageChannels.publishSubscribe(lobbyName).getObject();
        this.game = new Game();
    }

    public Collection<UserInfo> getPlayers() {
        return playersJoined.values();
    }

    public UserInfo getPlayer(UUID uuid) {
        return playersJoined.get(uuid);
    }

    public UserInfo getPlayer(String username) {
        return playersJoined.values().stream()
                .filter(somePlayer -> somePlayer.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public boolean isFull() {
        return playersJoined.size() == maxPlayers;
    }

    public synchronized void addPlayer(UserInfo player) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (lobbyStatus == LobbyStatus.ACTIVE)
            throw new IllegalLobbyStatusException(lobbyName, LobbyStatus.ACTIVE);
        var usernameTaken = playersJoined.values().stream()
                .anyMatch(somePlayer -> somePlayer.getUsername().equals(player.getUsername()));
        if (usernameTaken)
            throw new UsernameTakenException(lobbyName, player.getUsername());
        playersJoined.put(player.getUuid(), player);
        game.addCharacter(player.getUsername(), new CharacterImpl());
        channel.subscribe(player.getSubscription());
        send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.CONNECTED));
        send(new LobbyFullUpdateDTO(playersJoined.values()));

        gameEventService.publishPlayerUpdate(this, player, LobbyPlayerUpdateAction.CONNECTED);
    }

    public synchronized boolean removePlayer(UserInfo player) {
        if (playersJoined.remove(player.getUuid()) != null) {
            game.removeCharacter(player.getUsername());
            player.send(new KickedDTO());
            send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.DISCONNECTED));
            send(new LobbyFullUpdateDTO(playersJoined.values()));
            channel.unsubscribe(player.getSubscription());
            player.toIdle();

            gameEventService.publishPlayerUpdate(this, player, LobbyPlayerUpdateAction.DISCONNECTED);

            return true;
        }
        return false;
    }

    public synchronized boolean kickPlayer(UserInfo player) {
        var removed = removePlayer(player);
        if (removed)
            player.send(new KickedDTO());
        return removed;
    }

    public synchronized void start() {
        if (lobbyStatus != LobbyStatus.IDLE)
            throw new IllegalLobbyStatusException(lobbyName, lobbyStatus);
        if (playersJoined.size() < minPlayers)
            throw new NotEnoughPlayersException(lobbyName, minPlayers - playersJoined.size());
        lobbyStatus = LobbyStatus.ACTIVE;
        game.start();
        send(new LobbyStatusChangedDTO(LobbyStatus.ACTIVE));

        gameEventService.publishLobbyStatusUpdate(this, LobbyStatus.ACTIVE);
    }

    public synchronized void close() {
        for (var player : new ArrayList<>(playersJoined.values()))
            removePlayer(player);
        send(new LobbyStatusChangedDTO(LobbyStatus.CLOSED));

        gameEventService.publishLobbyStatusUpdate(this, LobbyStatus.CLOSED);
    }

    public void send(EventDTO eventMessage) {
        channel.send(new GenericMessage<>(eventMessage));
    }
}
