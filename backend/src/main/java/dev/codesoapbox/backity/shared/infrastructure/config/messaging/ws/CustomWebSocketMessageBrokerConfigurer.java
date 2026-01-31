package dev.codesoapbox.backity.shared.infrastructure.config.messaging.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

    private final JsonMapper jsonMapper;

    /**
     * Otherwise the default uses a {@link MessageConverter} that uses its own ObjectMapper, which is not configured
     * for LocalDate conversion, etc.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        var messageConverter = new JacksonJsonMessageConverter(jsonMapper);
        messageConverters.add(messageConverter);

        return true;
    }
}
