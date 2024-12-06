package dev.codesoapbox.backity.testing.messaging;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestMessageChannel extends AbstractSubscribableChannel {

    private final List<Message<?>> messages = new ArrayList<>();

    public String receiveMessage(String destination) {
        return messages.stream()
                .filter(msg -> destination.equals(
                        msg.getHeaders().get("simpDestination", String.class)))
                .map(Message::getPayload)
                .map(payload -> new String((byte[]) payload, StandardCharsets.UTF_8))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        this.messages.add(message);
        return true;
    }

    public void clearMessages() {
        messages.clear();
    }
}
