package net.kravuar.shmanchkin.domain.model.game;

import lombok.EqualsAndHashCode;
import lombok.Getter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Validated
@EqualsAndHashCode(of = "lobbyName")
public class Game {
    public Game(@Length(min = 3, max = 30) String lobbyName, Player owner) {
        this.lobbyName = lobbyName;
        this.owner = owner;
    }

    public static final int MinPlayers = 4;
    public static final int MaxPlayers = 6;
    private final Map<String, Player> playersJoined = new HashMap<>();
    private final SubscribableChannel channel = MessageChannels.publishSubscribe().getObject();
    @Getter
    private final String lobbyName;
    @Getter
    private final Player owner;
    @Getter
    private GameStatus status = GameStatus.IDLE;

    public Map<String, Player> getPlayers() {
        return Collections.unmodifiableMap(playersJoined);
    }

    public boolean isFull() {
        return playersJoined.size() == MaxPlayers;
    }

    public void addPlayer(Player player) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (status == GameStatus.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.putIfAbsent(player.getUsername(), player) != null)
            throw new UsernameTakenException(lobbyName, player.getUsername());
        player.subscribe(channel);
        send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.CONNECTED));
        send(new LobbyFullUpdateDTO(getPlayers().values()));
    }

    public void removePlayer(Player player) {
        var removed = playersJoined.remove(player.getUsername());
        if (removed != null) {
            send(new LobbyUpdateDTO(player, LobbyPlayerUpdateAction.DISCONNECTED));
            send(new LobbyFullUpdateDTO(getPlayers().values()));
            removed.send(new KickedDTO());
            player.unsubscribe(channel);
            player.toIdle();

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
