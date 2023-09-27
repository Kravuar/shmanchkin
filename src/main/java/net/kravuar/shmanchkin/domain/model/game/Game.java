package net.kravuar.shmanchkin.domain.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Game {
    private final SubscribableChannel channel = MessageChannels.publishSubscribe().getObject();
    @Getter
    private final String lobbyName;
    @Getter
    private final String ownerName;
    @Getter
    private final int maxPlayers;
    @Getter
    private final List<Player> playersJoined = new ArrayList<>();

    public boolean isFull() {
        return playersJoined.size() == maxPlayers;
    }


    public synchronized void subscribe(MessageHandler handler, String username) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        channel.subscribe(handler);
        playersJoined.add(new Player(username));
    }

    public void unsubscribe(MessageHandler handler, String username) {
        channel.unsubscribe(handler);
        playersJoined.removeIf(player -> player.getUsername().equals(username));
    }

    public void publishEvent(GenericMessage<GameEvent> eventMessage) {
        channel.send(eventMessage);
    }
}
