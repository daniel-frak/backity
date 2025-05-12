package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.infrastructure.config.FileDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.infrastructure.config.GameControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefile.application.usecases.*;
import dev.codesoapbox.backity.core.gamefile.infrastructure.config.GameFileControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.*;
import dev.codesoapbox.backity.shared.infrastructure.config.jpa.SharedControllerBeanConfig;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogControllerBeanConfig;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
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
        GogLibraryService.class,

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
public @interface ConfigureSharedControllerTestBeans {
}
