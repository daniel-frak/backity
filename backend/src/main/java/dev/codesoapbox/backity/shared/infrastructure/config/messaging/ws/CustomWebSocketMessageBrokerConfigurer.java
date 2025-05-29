package dev.codesoapbox.backity.shared.infrastructure.config.messaging.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

    private final ObjectMapper objectMapper;

    /**
     * Otherwise the default uses a {@link MessageConverter} that uses its own ObjectMapper, which is not configured
     * for LocalDate conversion, etc.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        var messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        messageConverters.add(messageConverter);

        return true;
    }
}
