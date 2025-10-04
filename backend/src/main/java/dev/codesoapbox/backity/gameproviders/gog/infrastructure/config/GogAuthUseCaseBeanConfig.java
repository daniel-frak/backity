package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.CheckGogAuthenticationUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.LogOutOfGogUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.RefreshGogAccessTokenUseCase;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class GogAuthUseCaseBeanConfig {

    @Bean
    AuthenticateGogUseCase authenticateGogUseCase(GogAuthService authService) {
        return new AuthenticateGogUseCase(authService);
    }

    @Bean
    CheckGogAuthenticationUseCase checkGogAuthenticationUseCase(GogAuthService authService) {
        return new CheckGogAuthenticationUseCase(authService);
    }

    @Bean
    RefreshGogAccessTokenUseCase refreshGogAccessTokenUseCase(GogAuthService authService) {
        return new RefreshGogAccessTokenUseCase(authService);
    }

    @Bean
    LogOutOfGogUseCase logOutOfGogUseCase(GogAuthService authService) {
        return new LogOutOfGogUseCase(authService);
    }
}
