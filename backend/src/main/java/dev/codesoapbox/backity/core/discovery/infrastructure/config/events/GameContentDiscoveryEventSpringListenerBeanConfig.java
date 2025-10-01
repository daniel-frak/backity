package dev.codesoapbox.backity.core.discovery.infrastructure.config.events;


import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryProgressChangedEventHandler;
import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStartedEventHandler;
import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStoppedEventHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring.GameContentDiscoveryStoppedEventSpringListener;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring.GameContentDiscoveryProgressChangedEventSpringListener;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring.GameContentDiscoveryStartedEventSpringListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringEventListenerBeanConfiguration
public class GameContentDiscoveryEventSpringListenerBeanConfig {

    @Bean
    GameContentDiscoveryStartedEventSpringListener gameContentDiscoveryStartedEventSpringListener(
            GameContentDiscoveryStartedEventHandler eventHandler) {
        return new GameContentDiscoveryStartedEventSpringListener(eventHandler);
    }

    @Bean
    GameContentDiscoveryProgressChangedEventSpringListener
    gameContentDiscoveryProgressChangedEventSpringListener(
            GameContentDiscoveryProgressChangedEventHandler eventHandler) {
        return new GameContentDiscoveryProgressChangedEventSpringListener(eventHandler);
    }

    @Bean
    GameContentDiscoveryStoppedEventSpringListener gameContentDiscoveryStoppedEventSpringListener(
            GameContentDiscoveryStoppedEventHandler eventHandler) {
        return new GameContentDiscoveryStoppedEventSpringListener(eventHandler);
    }
}
