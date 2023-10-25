package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.dto.events.GameEventDTO;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsActiveException;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Game {
    public enum Status {
        IDLE,
        ACTIVE
    }

    private final Map<String, UserInfo> playersJoined = new HashMap<>();
    private final SubscribableChannel channel = MessageChannels.publishSubscribe().getObject();
    @Getter
    private final String lobbyName;
    @Getter
    private final UserInfo owner;
    @Getter
    private final int maxPlayers;
    @Getter
    private Status status = Status.IDLE;

    public Collection<UserInfo> getPlayers() {
        return Collections.unmodifiableCollection(playersJoined.values());
    }

    public boolean isFull() {
        return playersJoined.size() == maxPlayers;
    }

    public void subscribe(MessageHandler handler, UserInfo userInfo) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (status == Status.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        if (playersJoined.putIfAbsent(userInfo.getUsername(), userInfo) != null)
            throw new UsernameTakenException(lobbyName, userInfo.getUsername());
        channel.subscribe(handler);
    }

    public void unsubscribe(MessageHandler handler, UserInfo userInfo) {
        channel.unsubscribe(handler);
        playersJoined.remove(userInfo.getUsername());
    }

    public void start() {
        if (status == Status.ACTIVE)
            throw new GameIsActiveException(lobbyName);
        status = Status.ACTIVE;
    }

    public void publishEvent(GenericMessage<GameEventDTO> eventMessage) {
        channel.send(eventMessage);
    }
}
