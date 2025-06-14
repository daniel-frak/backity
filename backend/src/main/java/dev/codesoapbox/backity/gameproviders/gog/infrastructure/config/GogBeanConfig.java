package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.DownloadService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.*;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth.GogAuthClient;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth.GogAuthSpringService;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogConfigUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
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

    protected static final String USER_AUTH_URL_SUFFIX =
            "/auth?client_id=46899977096215655" +
            "&redirect_uri=https%3A%2F%2Fembed.gog.com%2Fon_login_success%3Forigin%3Dclient" +
            "&response_type=code&layout=client2";

    private final GogProperties gogProperties;

    @Bean
    GetGogConfigUseCase getGogConfigUseCase() {
        var gogConfigInfo = new GogConfigInfo(gogProperties.auth().baseUrl() + USER_AUTH_URL_SUFFIX);
        return new GetGogConfigUseCase(gogConfigInfo);
    }

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
                                     Clock clock) {
        return new GogEmbedWebClient(webClientEmbed, authService, clock);
    }

    @Bean
    DownloadService urlFileDownloader() {
        return new DownloadService();
    }

    @Bean
    GogFileBackupService gogFileBackupService(GogEmbedWebClient gogEmbedClient, GogAuthService authService,
                                              DownloadService downloadService) {
        return new GogFileBackupService(gogEmbedClient, authService, downloadService);
    }

    @Bean
    GogFileDiscoveryService gogFileDiscoveryService(GogEmbedWebClient gogEmbedClient) {
        GogGameWithFilesMapper mapper = Mappers.getMapper(GogGameWithFilesMapper.class);
        return new GogFileDiscoveryService(gogEmbedClient, mapper);
    }
}
