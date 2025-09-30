package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthClient;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthSpringService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogFileBackupService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogFileDiscoveryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogGameWithFilesMapper;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GogBeanConfig {

    private final GogProperties gogProperties;

    @Bean
    GogAuthClient gogAuthClient(@Qualifier("gogAuth") WebClient webClientAuth) {
        return new GogAuthClient(webClientAuth,
                gogProperties.clientId(),
                gogProperties.clientSecret(),
                gogProperties.auth().redirectUri());
    }

    @Bean
    GogAuthService gogAuthService(GogAuthClient gogAuthClient) {
        return new GogAuthSpringService(gogAuthClient);
    }

    @Bean
    GogEmbedWebClient gogEmbedClient(@Qualifier("gogEmbed") WebClient webClientEmbed, GogAuthService authService,
                                     DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory,
                                     Clock clock) {
        return new GogEmbedWebClient(webClientEmbed, authService, dataBufferFluxTrackableFileStreamFactory, clock);
    }

    @Bean
    GogFileBackupService gogFileBackupService(GogEmbedWebClient gogEmbedClient, GogAuthService authService) {
        return new GogFileBackupService(gogEmbedClient, authService);
    }

    @Bean
    GogFileDiscoveryService gogFileDiscoveryService(GogEmbedWebClient gogEmbedClient) {
        GogGameWithFilesMapper mapper = Mappers.getMapper(GogGameWithFilesMapper.class);
        return new GogFileDiscoveryService(gogEmbedClient, mapper);
    }
}
