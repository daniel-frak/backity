package dev.codesoapbox.backity.testing.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class TestMessageChannel extends AbstractSubscribableChannel {

    private final JsonMapper jsonMapper;
    private final List<Message<?>> messages = new ArrayList<>();

    public void assertPublishedWebSocketEvent(String destination, String expectedJson) {
        String receivedMessage = receiveMessage(destination);
        assertThat(receivedMessage).isNotNull();
        assertThat(jsonMapper.readTree(receivedMessage))
                .isEqualTo(jsonMapper.readTree(expectedJson));
    }

    private String receiveMessage(String destination) {
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
