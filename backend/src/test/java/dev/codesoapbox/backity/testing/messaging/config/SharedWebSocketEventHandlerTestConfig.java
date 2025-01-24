package dev.codesoapbox.backity.testing.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@TestConfiguration
public class SharedWebSocketEventHandlerTestConfig {

    @Bean
    TestMessageChannel testMessageChannel(ObjectMapper objectMapper) {
        return new TestMessageChannel(objectMapper);
    }

    @Bean
    SimpMessagingTemplate simpMessagingTemplate(TestMessageChannel messageChannel) {
        var simpMessagingTemplate = new SimpMessagingTemplate(messageChannel);
        var messageConverter = new MappingJackson2MessageConverter();
        simpMessagingTemplate.setMessageConverter(messageConverter);

        return simpMessagingTemplate;
    }
}
