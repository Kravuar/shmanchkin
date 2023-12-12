package net.kravuar.shmanchkin.domain.model.game;

import net.kravuar.shmanchkin.domain.model.events.game.EscapeAttemptedEvent;
import net.kravuar.shmanchkin.domain.model.events.game.GameEvent;
import net.kravuar.shmanchkin.domain.model.events.game.GameStageChangedEvent;
import net.kravuar.shmanchkin.domain.model.game.character.Character;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

public class SubscribableGame extends Game implements SubscribableChannel {
    private final SubscribableChannel channel;

    public SubscribableGame(String name) {
        this.channel = MessageChannels
                .publishSubscribe(name)
                .getObject();
    }

    @Override
    public void advanceTurnStage() {
        super.advanceTurnStage();
        send(new GameStageChangedEvent(this, turnStage));
    }

    @Override
    public boolean escapeBattle(Character character) {
        var result = super.escapeBattle(character);
        send(new EscapeAttemptedEvent(this, character, result));
        return result;
    }

    private void send(GameEvent gameEvent) {
        channel.send(new GenericMessage<>(gameEvent));
    }

    @Override
    public boolean subscribe(MessageHandler handler) {
        return channel.subscribe(handler);
    }
    @Override
    public boolean unsubscribe(MessageHandler handler) {
        return channel.unsubscribe(handler);
    }

    @Override
    public boolean send(Message<?> message) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean send(Message<?> message, long timeout) {
        throw new UnsupportedOperationException();
    }
}
