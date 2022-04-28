package dev.codesoapbox.backity.core.shared.config;

import dev.codesoapbox.backity.core.shared.adapters.driven.spring.SpringMessageService;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class SharedBeanConfig {

    @Bean
    MessageService messageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new SpringMessageService(simpMessagingTemplate);
    }
}
