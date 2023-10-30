package net.kravuar.shmanchkin.domain.model.game;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.events.*;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsActiveException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.NotEnoughPlayersException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import org.hibernate.validator.constraints.Length;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Validated
@EqualsAndHashCode(of = "lobbyName")
public class Game {
    public Game(@Length(min = 3, max = 30) String lobbyName, UserInfo owner) {
        this.lobbyName = lobbyName;
        this.owner = owner;
    }

    public static final int MinPlayers = 4;
    public static final int MaxPlayers = 6;
    private final Map<String, UserInfo> playersJoined = new HashMap<>();
    private final SubscribableChannel channel = MessageChannels.publishSubscribe().getObject();
    @Getter
    private final String lobbyName;
    @Getter
    private final UserInfo owner;
    @Getter
    private GameStatus status = GameStatus.IDLE;

    public Map<String, UserInfo> getPlayers() {
        return Collections.unmodifiableMap(playersJoined);
    }

    public boolean isFull() {
        return playersJoined.size() == MaxPlayers;
    }

    public void addPlayer(UserInfo userInfo) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (status == GameStatus.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.putIfAbsent(userInfo.getUsername(), userInfo) != null)
            throw new UsernameTakenException(lobbyName, userInfo.getUsername());
        userInfo.subscribe(channel);
        send(new LobbyUpdateDTO(userInfo, LobbyPlayerUpdateAction.CONNECTED));
        send(new LobbyFullUpdateDTO(getPlayers().values()));
    }

    public UserInfo getPlayer(String username) {
        return playersJoined.get(username);
    }

    public UserInfo getPlayer(UUID uuid) {
        return playersJoined.values().stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void removePlayer(UserInfo userInfo) {
        var removed = playersJoined.remove(userInfo.getUsername());
        if (removed != null) {
            send(new LobbyUpdateDTO(userInfo, LobbyPlayerUpdateAction.DISCONNECTED));
            send(new LobbyFullUpdateDTO(getPlayers().values()));
            removed.send(new KickedDTO());
            userInfo.unsubscribe(channel);
            userInfo.toIdle();

//            Since reconnect is not a thing, yeah.
            if (removed == owner)
                close();
        }
    }

    public void start() {
        if (status == GameStatus.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.size() < MinPlayers)
            throw new NotEnoughPlayersException(lobbyName, MinPlayers - playersJoined.size());
        status = GameStatus.ACTIVE;
        send(new GameStatusChangedDTO(GameStatus.ACTIVE));
//        TODO: Other game init stuff
    }

    public void close() {
        for (var player: new ArrayList<>(playersJoined.values()))
            removePlayer(player);
        send(new GameStatusChangedDTO(GameStatus.CLOSED));
    }

    public void send(EventDTO eventMessage) {
        channel.send(new GenericMessage<>(eventMessage));
    }
}
