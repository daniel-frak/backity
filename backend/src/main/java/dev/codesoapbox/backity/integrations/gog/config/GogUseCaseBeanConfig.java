package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.application.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.integrations.gog.application.GetGogLibrarySizeUseCase;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogUseCaseBeanConfig {

    @Bean
    public GetGogLibrarySizeUseCase getGogLibrarySizeUseCase(GogEmbedClient gogEmbedClient) {
        return new GetGogLibrarySizeUseCase(gogEmbedClient);
    }

    @Bean
    public GetGogGameDetailsUseCase getGogGameDetailsUseCase(GogEmbedClient gogEmbedClient) {
        return new GetGogGameDetailsUseCase(gogEmbedClient);
    }
}
