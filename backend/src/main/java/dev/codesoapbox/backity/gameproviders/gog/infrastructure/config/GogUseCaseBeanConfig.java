package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogLibrarySizeUseCase;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogUseCaseBeanConfig {

    @Bean
    public GetGogLibrarySizeUseCase getGogLibrarySizeUseCase(GogLibraryService gogLibraryService) {
        return new GetGogLibrarySizeUseCase(gogLibraryService);
    }

    @Bean
    public GetGogGameDetailsUseCase getGogGameDetailsUseCase(GogLibraryService gogLibraryService) {
        return new GetGogGameDetailsUseCase(gogLibraryService);
    }
}
