package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.core.files.downloading.domain.services.FileManager;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.GogFileDiscoveryService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.GogFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.UrlFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthClient;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed.GogEmbedClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

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
        return new GogAuthService(gogAuthClient);
    }

    @Bean
    GogEmbedClient gogEmbedClient(@Qualifier("gogEmbed") WebClient webClientEmbed, GogAuthService authService) {
        return new GogEmbedClient(webClientEmbed, authService);
    }

    @Bean
    UrlFileDownloader enqueuedFileDownloader(FileManager fileManager) {
        return new UrlFileDownloader(fileManager);
    }

    @Bean
    GogFileDownloader gogFileDownloader(GogEmbedClient gogEmbedClient, GogAuthService authService,
                                        UrlFileDownloader urlFileDownloader) {
        return new GogFileDownloader(gogEmbedClient, authService, urlFileDownloader);
    }

    @Bean
    GogFileDiscoveryService gogFileDiscoveryService(GogEmbedClient gogEmbedClient) {
        return new GogFileDiscoveryService(gogEmbedClient);
    }
}
