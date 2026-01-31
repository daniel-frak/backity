package dev.codesoapbox.backity.testing.messaging.config;

import dev.codesoapbox.backity.shared.infrastructure.config.messaging.ws.CustomWebSocketMessageBrokerConfigurer;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tools.jackson.databind.json.JsonMapper;

@Import(CustomWebSocketMessageBrokerConfigurer.class)
@TestConfiguration
public class SharedSpringWebSocketEventListenerTestConfig {

    @Bean
    TestMessageChannel testMessageChannel(JsonMapper jsonMapper) {
        return new TestMessageChannel(jsonMapper);
    }

    @Bean
    SimpMessagingTemplate simpMessagingTemplate(TestMessageChannel messageChannel,
                                                CompositeMessageConverter brokerMessageConverter) {
        var simpMessagingTemplate = new SimpMessagingTemplate(messageChannel);
        simpMessagingTemplate.setMessageConverter(brokerMessageConverter);

        return simpMessagingTemplate;
    }
}
