package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileBackupService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileDiscoveryServiceGame;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.UrlFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthClient;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.application.usecases.GetGogConfigUseCase;
import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    UrlFileDownloader urlFileDownloader(FileManager fileManager) {
        return new UrlFileDownloader(fileManager);
    }

    @Bean
    GogFileBackupService gogFileBackupService(GogEmbedWebClient gogEmbedClient, GogAuthService authService,
                                              UrlFileDownloader urlFileDownloader) {
        return new GogFileBackupService(gogEmbedClient, authService, urlFileDownloader);
    }

    @Bean
    GogFileDiscoveryServiceGame gogFileDiscoveryService(GogEmbedWebClient gogEmbedClient) {
        return new GogFileDiscoveryServiceGame(gogEmbedClient);
    }
}
