package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.*;

@RequiredArgsConstructor
public class Game {
    private final SubscribableChannel channel = MessageChannels.publishSubscribe().getObject();
    @Getter
    private final String lobbyName;
    @Getter
    private final String ownerName;
    @Getter
    private final int maxPlayers;
    private final Map<String, Player> playersJoined = new HashMap<>();

    public Collection<Player> getPlayers() {
        return Collections.unmodifiableCollection(playersJoined.values());
    }

    public boolean isFull() {
        return playersJoined.size() == maxPlayers;
    }

    public void subscribe(MessageHandler handler, UserInfo userInfo) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        var username = userInfo.getUsername();
        if (playersJoined.putIfAbsent(username, new Player(username)) != null)
            throw new UsernameTakenException(lobbyName, username);
        channel.subscribe(handler);
    }

    public void unsubscribe(MessageHandler handler, UserInfo userInfo) {
        channel.unsubscribe(handler);
        playersJoined.remove(userInfo.getUsername());
    }

    public void publishEvent(GenericMessage<GameEvent> eventMessage) {
        channel.send(eventMessage);
    }
}
