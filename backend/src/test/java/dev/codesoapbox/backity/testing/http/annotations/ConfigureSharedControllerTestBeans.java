package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.core.backuptarget.application.GetBackupTargetsUseCase;
import dev.codesoapbox.backity.core.backuptarget.config.BackupTargetControllerBeanConfig;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.infrastructure.config.GameContentDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.filecopy.infrastructure.config.FileCopyControllerBeanConfig;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.infrastructure.config.GameControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.infrastructure.config.GameFileControllerBeanConfig;
import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.*;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogControllerBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.SharedControllerBeanConfig;
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
        GameContentDiscoveryControllerBeanConfig.class,
        GogControllerBeanConfig.class,
        GameControllerBeanConfig.class,
        GameFileControllerBeanConfig.class,
        FileCopyControllerBeanConfig.class,
        BackupTargetControllerBeanConfig.class
})
@MockitoBean(types = {
        // Common
        EntityManager.class,

        // Specific
        GameFileRepository.class,
        GameContentDiscoveryService.class,
        LogService.class,
        GogAuthService.class,
        GogLibraryService.class,

        // Use cases
        GetGamesWithFilesUseCase.class,
        StartGameContentDiscoveryUseCase.class,
        StopGameContentDiscoveryUseCase.class,
        GetGameContentDiscoveryStatusListUseCase.class,
        EnqueueFileCopyUseCase.class,
        DeleteFileCopyUseCase.class,
        DownloadFileCopyUseCase.class,
        GetCurrentlyDownloadingFileCopyUseCase.class,
        GetEnqueuedFileCopiesUseCase.class,
        GetProcessedFileCopiesUseCase.class,
        GetGogConfigUseCase.class,
        AuthenticateGogUseCase.class,
        CheckGogAuthenticationUseCase.class,
        RefreshGogAccessTokenUseCase.class,
        LogOutOfGogUseCase.class,
        GetGogLibrarySizeUseCase.class,
        GetGogGameDetailsUseCase.class,
        GetLogsUseCase.class,
        GetBackupTargetsUseCase.class
})
public @interface ConfigureSharedControllerTestBeans {
}
