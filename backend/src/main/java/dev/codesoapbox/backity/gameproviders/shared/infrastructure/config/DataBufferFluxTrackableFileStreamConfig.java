package dev.codesoapbox.backity.gameproviders.shared.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.config.slices.GameProviderServiceConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@GameProviderServiceConfiguration
public class DataBufferFluxTrackableFileStreamConfig {

    @Bean
    DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory(
            @Value("${backity.replication.max-retry-attempts}") int maxRetryAttempts,
            @Value("${backity.replication.retry-backoff-in-seconds}") int retryBackoffInSeconds) {
        Duration retryBackoff = Duration.ofSeconds(retryBackoffInSeconds);
        return new DataBufferFluxTrackableFileStreamFactory(maxRetryAttempts, retryBackoff);
    }
}
