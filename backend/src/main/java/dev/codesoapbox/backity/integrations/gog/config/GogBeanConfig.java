package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileBackupService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileDiscoveryServiceGame;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.UrlFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthClient;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;

@Slf4j
@Configuration
public class GogBeanConfig {

    private final String clientId;
    private final String clientSecret;

    public GogBeanConfig(@Value("${gog.client-id}") String clientId,
                         @Value("${gog.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Bean
    GogAuthClient gogAuthClient(@Qualifier("gogAuth") WebClient webClientAuth,
                                @Value("${gog.embed.redirect-uri}") String redirectUri) {
        return new GogAuthClient(webClientAuth, clientId, clientSecret, redirectUri);
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
    UrlFileDownloader urlFileDownloader(FileManager fileManager, BackupProgressFactory backupProgressFactory) {
        return new UrlFileDownloader(fileManager, backupProgressFactory);
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
