package net.kravuar.shmanchkin.domain.model.gameLobby;

import lombok.NonNull;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.events.GameEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.LobbyStatusChangedEvent;
import net.kravuar.shmanchkin.domain.model.events.gameLobby.PlayerLobbyUpdateEvent;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

public class SubscribableGameLobby extends GameLobby implements SubscribableChannel {
    private final SubscribableChannel channel;

    public SubscribableGameLobby(String lobbyName, UserInfo owner, int minPlayers, int maxPlayers) {
        super(lobbyName, owner, minPlayers, maxPlayers);
        this.channel = MessageChannels.publishSubscribe(lobbyName).getObject();
    }

    @Override
    public void addPlayer(UserInfo player) {
        super.addPlayer(player);

        send(new PlayerLobbyUpdateEvent(this, player, LobbyPlayerUpdateAction.CONNECTED));
    }
    @Override
    public boolean removePlayer(UserInfo player, boolean kicked) {
        var result = super.removePlayer(player, kicked);
        if (result)
            send(new PlayerLobbyUpdateEvent(this, player, kicked
                    ? LobbyPlayerUpdateAction.KICKED
                    : LobbyPlayerUpdateAction.DISCONNECTED
                )
            );

        return result;
    }

    @Override
    public void start() {
        super.start();

        send(new LobbyStatusChangedEvent(this, LobbyStatus.ACTIVE));
    }
    @Override
    public void close() {
        super.close();
        send(new LobbyStatusChangedEvent(this, LobbyStatus.CLOSED));
    }

    private void send(GameEvent gameEvent) {
        channel.send(new GenericMessage<>(gameEvent));
    }

    @Override
    public boolean subscribe(@NonNull MessageHandler handler) {
        return channel.subscribe(handler);
    }
    @Override
    public boolean unsubscribe(@NonNull MessageHandler handler) {
        return channel.unsubscribe(handler);
    }

    @Override
    public boolean send(@NonNull Message<?> message) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean send(@NonNull Message<?> message, long timeout) {
        throw new UnsupportedOperationException();
    }
}
