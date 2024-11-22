package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.FileBackupEventPublisher;
import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileBackupServiceGame;
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
    GogEmbedWebClient gogEmbedClient(@Qualifier("gogEmbed") WebClient webClientEmbed, GogAuthService authService) {
        return new GogEmbedWebClient(webClientEmbed, authService);
    }

    @Bean
    UrlFileDownloader enqueuedFileDownloader(FileManager fileManager,
                                             FileBackupEventPublisher fileBackupEventPublisher) {
        return new UrlFileDownloader(fileManager, i -> getProgressInfoConsumer(fileBackupEventPublisher, i),
                BackupProgress::new);
    }

    private void getProgressInfoConsumer(FileBackupEventPublisher fileBackupEventPublisher, ProgressInfo i) {
        fileBackupEventPublisher.publishFileBackupProgressChangedEvent(
                new FileBackupProgress(i.percentage(), i.timeLeft().toSeconds()));
    }

    @Bean
    GogFileBackupServiceGame gogFileDownloader(GogEmbedWebClient gogEmbedClient, GogAuthService authService,
                                               UrlFileDownloader urlFileDownloader) {
        return new GogFileBackupServiceGame(gogEmbedClient, authService, urlFileDownloader);
    }

    @Bean
    GogFileDiscoveryServiceGame gogFileDiscoveryService(GogEmbedWebClient gogEmbedClient) {
        return new GogFileDiscoveryServiceGame(gogEmbedClient);
    }
}
