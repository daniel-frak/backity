package dev.codesoapbox.backity.core.discovery.infrastructure.config.events;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.*;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.DomainEventHandlerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@DomainEventHandlerBeanConfiguration
public class GameContentDiscoveryEventHandlerBeanConfig {

    @Bean
    GameContentDiscoveryStartedEventHandler gameContentDiscoveryStartedEventHandler(
            GameContentDiscoveryStartedEventExternalForwarder eventForwarder) {
        return new GameContentDiscoveryStartedEventHandler(eventForwarder);
    }

    @Bean
    GameContentDiscoveryProgressChangedEventHandler gameContentDiscoveryProgressChangedEventHandler(
            GameContentDiscoveryProgressChangedEventExternalForwarder eventForwarder) {
        return new GameContentDiscoveryProgressChangedEventHandler(eventForwarder);
    }

    @Bean
    GameContentDiscoveryStoppedEventHandler gameContentDiscoveryStoppedEventHandler(
            GameContentDiscoveryStoppedEventExternalForwarder eventForwarder) {
        return new GameContentDiscoveryStoppedEventHandler(eventForwarder);
    }
}
