package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileBackupMessageService;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileManager;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileBackupService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.GogFileDiscoveryService;
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
    GogAuthClient gogAuthClient(@Qualifier("gogAuth") WebClient webClientAuth) {
        return new GogAuthClient(webClientAuth, clientId, clientSecret);
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
                                             FileBackupMessageService fileBackupMessageService) {
        return new UrlFileDownloader(fileManager,
                i -> fileBackupMessageService.sendProgress(
                        new FileBackupProgress(i.percentage(), i.timeLeft().toSeconds())));
    }

    @Bean
    GogFileBackupService gogFileDownloader(GogEmbedWebClient gogEmbedClient, GogAuthService authService,
                                           UrlFileDownloader urlFileDownloader) {
        return new GogFileBackupService(gogEmbedClient, authService, urlFileDownloader);
    }

    @Bean
    GogFileDiscoveryService gogFileDiscoveryService(GogEmbedWebClient gogEmbedClient) {
        return new GogFileDiscoveryService(gogEmbedClient);
    }
}
