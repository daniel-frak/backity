package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthClient;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthSpringService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogFileBackupService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogFileDiscoveryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogGameWithFilesMapper;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.GogEmbedWebClient;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetGameDetailsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibraryGameIdsGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.GetLibrarySizeGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.InitializeProgressAndStreamFileGogEmbedOperation;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.config.slices.GameProviderServiceConfiguration;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;

@GameProviderServiceConfiguration
@RequiredArgsConstructor
public class GogServiceBeanConfig {

    private final GogProperties gogProperties;

    @Bean
    GogAuthClient gogAuthClient(@Qualifier("gogAuth") WebClient webClientAuth) {
        return new GogAuthClient(webClientAuth,
                gogProperties.clientId(),
                gogProperties.clientSecret(),
                gogProperties.auth().redirectUri());
    }

    @Bean
    GogAuthSpringService gogAuthService(GogAuthClient gogAuthClient) {
        return new GogAuthSpringService(gogAuthClient);
    }

    @Bean
    GogEmbedWebClient gogEmbedClient(
            @Qualifier("gogEmbed") WebClient webClientEmbed,
            JsonMapper jsonMapper,
            GogAuthService authService,
            DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory,
            Clock clock
    ) {
        var getLibraryGameIdsOperation = new GetLibraryGameIdsGogEmbedOperation(
                webClientEmbed, authService);
        var getGameDetailsOperation = new GetGameDetailsGogEmbedOperation(
                webClientEmbed, authService, jsonMapper);
        var getLibrarySizeOperation = new GetLibrarySizeGogEmbedOperation(
                getLibraryGameIdsOperation, getGameDetailsOperation);
        var initializeProgressAndStreamFileOperation = new InitializeProgressAndStreamFileGogEmbedOperation(
                webClientEmbed, authService, dataBufferFluxTrackableFileStreamFactory, clock);

        return new GogEmbedWebClient(getLibrarySizeOperation, getGameDetailsOperation,
                getLibraryGameIdsOperation, initializeProgressAndStreamFileOperation);
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
