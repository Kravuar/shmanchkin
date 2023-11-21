package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.events.*;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.IllegalLobbyStatusException;
import net.kravuar.shmanchkin.domain.model.exceptions.NotEnoughPlayersException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import org.hibernate.validator.constraints.Length;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameLobby {
    public static final int MinPlayers = 1;
    public static final int MaxPlayers = 6;

    private final Game game;
    @Getter
    private final String lobbyName;
    @Getter
    private final UserInfo owner;
    @Getter
    private LobbyStatus lobbyStatus = LobbyStatus.IDLE;
    private final SubscribableChannel channel;
    private final Map<UUID, UserInfo> playersJoined = new HashMap<>();

    public GameLobby(@Length(min = 3, max = 30) String lobbyName, UserInfo owner) {
        this.game = new Game();
        this.lobbyName = lobbyName;
        this.owner = owner;
        this.channel = MessageChannels.publishSubscribe(lobbyName).getObject();
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
        return playersJoined.size() == MaxPlayers;
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
        game.addCharacter(player.getUsername(), new Character());
        channel.subscribe(player.getSubscription());
        send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.CONNECTED));
        send(new LobbyFullUpdateDTO(playersJoined.values()));
    }

    public synchronized boolean removePlayer(UserInfo player) {
        if (playersJoined.remove(player.getUsername()) != null) {
            game.removeCharacter(player.getUsername());
            player.send(new KickedDTO());
            send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.DISCONNECTED));
            send(new LobbyFullUpdateDTO(playersJoined.values()));
            channel.unsubscribe(player.getSubscription());
            player.toIdle();

            return true;
        }
        return false;
    }

    public synchronized void start() {
        if (lobbyStatus != LobbyStatus.IDLE)
            throw new IllegalLobbyStatusException(lobbyName, lobbyStatus);
        if (playersJoined.size() < MinPlayers)
            throw new NotEnoughPlayersException(lobbyName, MinPlayers - playersJoined.size());
        lobbyStatus = LobbyStatus.ACTIVE;
        game.start();
        send(new LobbyStatusChangedDTO(LobbyStatus.ACTIVE));
    }

    public synchronized void close() {
        for (var player: playersJoined.values())
            removePlayer(player);
        send(new LobbyStatusChangedDTO(LobbyStatus.CLOSED));
    }

    public void send(EventDTO eventMessage) {
        channel.send(new GenericMessage<>(eventMessage));
    }
}
