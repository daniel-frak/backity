package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.config.FileDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.game.application.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.config.GameControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefile.application.*;
import dev.codesoapbox.backity.core.gamefile.config.GameFileControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.infrastructure.config.jpa.SharedControllerBeanConfig;
import dev.codesoapbox.backity.integrations.gog.application.*;
import dev.codesoapbox.backity.integrations.gog.config.GogControllerBeanConfig;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        // Common
        SharedControllerBeanConfig.class,
        FakeTimeBeanConfig.class,

        // Specific
        FileDiscoveryControllerBeanConfig.class,
        GogControllerBeanConfig.class,
        GameControllerBeanConfig.class,
        GameFileControllerBeanConfig.class
})
@MockitoBean(types = {
        // Common
        EntityManager.class,

        // Specific
        GameFileRepository.class,
        FileDiscoveryService.class,
        LogService.class,
        GogAuthService.class,
        GogEmbedClient.class,

        // Use cases
        GetGamesWithFilesUseCase.class,
        StartFileDiscoveryUseCase.class,
        StopFileDiscoveryUseCase.class,
        GetFileDiscoveryStatusListUseCase.class,
        EnqueueFileUseCase.class,
        DeleteFileUseCase.class,
        DownloadFileUseCase.class,
        GetCurrentlyDownloadingFileUseCase.class,
        GetDiscoveredFileListUseCase.class,
        GetEnqueuedFileListUseCase.class,
        GetProcessedFileListUseCase.class,
        GetGogConfigUseCase.class,
        AuthenticateGogUseCase.class,
        CheckGogAuthenticationUseCase.class,
        RefreshGogAccessTokenUseCase.class,
        LogOutOfGogUseCase.class,
        GetGogLibrarySizeUseCase.class,
        GetGogGameDetailsUseCase.class,
        GetLogsUseCase.class
})
public @interface ControllerTestBeans {
}
