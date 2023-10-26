package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.dto.events.EventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsActiveException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.NotEnoughPlayersException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import org.hibernate.validator.constraints.Length;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Validated
public class Game {
    public Game(@Length(min = 3, max = 30) String lobbyName, String ownerUsername) {
        this.lobbyName = lobbyName;
        this.owner = new Player(ownerUsername, this);
    }

    public enum Status {
        IDLE,
        ACTIVE
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
    private Status status = Status.IDLE;

    private void subscribe(MessageHandler handler, Player player) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (status == Status.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.putIfAbsent(player.getUsername(), player) != null)
            throw new UsernameTakenException(lobbyName, player.getUsername());
        channel.subscribe(handler);
        player.setChannelMessageHandler(handler);
    }

    private void unsubscribe(Player player) {
        channel.unsubscribe(player.getChannelMessageHandler());
        player.setChannelMessageHandler(null);
        playersJoined.remove(player.getUsername());
    }

    public Map<String, Player> getPlayers() {
        return Collections.unmodifiableMap(playersJoined);
    }

    public boolean isFull() {
        return playersJoined.size() == MaxPlayers;
    }

    public void addPlayer(MessageHandler handler, Player player) {
        subscribe(handler, player);
    }

    public void removePlayer(Player player) {
        unsubscribe(player);
    }

    public void start() {
        if (status == Status.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.size() < MinPlayers)
            throw new NotEnoughPlayersException(lobbyName, MinPlayers - playersJoined.size());
        status = Status.ACTIVE;
    }

    public void send(GenericMessage<EventDTO> eventMessage) {
        channel.send(eventMessage);
    }

    public void sendTo(Player player, GenericMessage<EventDTO> eventMessage) {
        player.getChannelMessageHandler().handleMessage(eventMessage);
    }
}
