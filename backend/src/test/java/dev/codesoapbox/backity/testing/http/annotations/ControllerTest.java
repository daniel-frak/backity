package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backuptarget.application.GetBackupTargetsUseCase;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryOverviewsUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.core.storagesolution.application.GetStorageSolutionStatusesUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.*;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import dev.codesoapbox.backity.testing.time.config.ResetClockTestExecutionListener;
import jakarta.persistence.EntityManager;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.*;

/**
 * Annotation for a controller test.
 * <p>
 * Mocks every use case and injects a fake clock bean.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for controller tests does not take a long time, it can lead to the context
 * cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all controller tests share a single application context should protect against cache eviction slowing
 * down the tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
@TestExecutionListeners(listeners = ResetClockTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Import({
        // Common
        FakeTimeBeanConfig.class
})
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = ControllerBeanConfiguration.class
        ),
        useDefaultFilters = false
)
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
        GetGameContentDiscoveryOverviewsUseCase.class,
        EnqueueFileCopyUseCase.class,
        CancelFileCopyUseCase.class,
        DeleteFileCopyUseCase.class,
        DownloadFileCopyUseCase.class,
        GetFileCopyQueueUseCase.class,
        GetGogConfigUseCase.class,
        AuthenticateGogUseCase.class,
        CheckGogAuthenticationUseCase.class,
        RefreshGogAccessTokenUseCase.class,
        LogOutOfGogUseCase.class,
        GetGogLibrarySizeUseCase.class,
        GetGogGameDetailsUseCase.class,
        GetLogsUseCase.class,
        GetBackupTargetsUseCase.class,
        GetStorageSolutionStatusesUseCase.class
})
public @interface ControllerTest {
}
