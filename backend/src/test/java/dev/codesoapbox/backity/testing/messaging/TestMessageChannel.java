package dev.codesoapbox.backity.testing.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class TestMessageChannel extends AbstractSubscribableChannel {

    private final ObjectMapper objectMapper;
    private final List<Message<?>> messages = new ArrayList<>();

    public void assertPublishedWebSocketEvent(String destination, String expectedJson) throws JsonProcessingException {
        String receivedMessage = receiveMessage(destination);
        assertThat(receivedMessage).isNotNull();
        assertThat(objectMapper.readTree(receivedMessage))
                .isEqualTo(objectMapper.readTree(expectedJson));
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
