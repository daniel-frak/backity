package dev.codesoapbox.backity.gameproviders.shared.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.config.slices.GameProviderServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@GameProviderServiceConfiguration
public class DataBufferFluxTrackableFileStreamConfig {

    @Bean
    DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory(
            ReplicationProperties replicationProperties) {
        Duration retryBackoff = Duration.ofSeconds(replicationProperties.retryBackoffInSeconds());
        return new DataBufferFluxTrackableFileStreamFactory(
                replicationProperties.retryBackoffInSeconds(), retryBackoff);
    }

    @ConfigurationProperties("backity.replication")
    public record ReplicationProperties(
            int maxRetryAttempts,
            int retryBackoffInSeconds
    ) {
    }
}
