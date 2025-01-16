package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.application.AuthenticateGogUseCase;
import dev.codesoapbox.backity.integrations.gog.application.CheckGogAuthenticationUseCase;
import dev.codesoapbox.backity.integrations.gog.application.LogOutOfGogUseCase;
import dev.codesoapbox.backity.integrations.gog.application.RefreshGogAccessTokenUseCase;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
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
