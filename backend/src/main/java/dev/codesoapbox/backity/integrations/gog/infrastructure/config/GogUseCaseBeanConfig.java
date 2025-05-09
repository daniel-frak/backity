package dev.codesoapbox.backity.integrations.gog.infrastructure.config;

import dev.codesoapbox.backity.integrations.gog.application.usecases.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.integrations.gog.application.usecases.GetGogLibrarySizeUseCase;
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
