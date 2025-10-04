package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogConfigUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogLibrarySizeUseCase;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@UseCaseBeanConfiguration
public class GogUseCaseBeanConfig {

    private static final String USER_AUTH_URL_SUFFIX =
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
    GetGogLibrarySizeUseCase getGogLibrarySizeUseCase(GogLibraryService gogLibraryService) {
        return new GetGogLibrarySizeUseCase(gogLibraryService);
    }

    @Bean
    GetGogGameDetailsUseCase getGogGameDetailsUseCase(GogLibraryService gogLibraryService) {
        return new GetGogGameDetailsUseCase(gogLibraryService);
    }
}
