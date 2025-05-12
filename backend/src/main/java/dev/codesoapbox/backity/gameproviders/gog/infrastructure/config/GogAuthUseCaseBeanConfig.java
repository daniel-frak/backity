package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.CheckGogAuthenticationUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.LogOutOfGogUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.RefreshGogAccessTokenUseCase;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogAuthUseCaseBeanConfig {

    @Bean
    public AuthenticateGogUseCase authenticateGogUseCase(GogAuthService authService) {
        return new AuthenticateGogUseCase(authService);
    }

    @Bean
    public CheckGogAuthenticationUseCase checkGogAuthenticationUseCase(GogAuthService authService) {
        return new CheckGogAuthenticationUseCase(authService);
    }

    @Bean
    public RefreshGogAccessTokenUseCase refreshGogAccessTokenUseCase(GogAuthService authService) {
        return new RefreshGogAccessTokenUseCase(authService);
    }

    @Bean
    public LogOutOfGogUseCase logOutOfGogUseCase(GogAuthService authService) {
        return new LogOutOfGogUseCase(authService);
    }
}
