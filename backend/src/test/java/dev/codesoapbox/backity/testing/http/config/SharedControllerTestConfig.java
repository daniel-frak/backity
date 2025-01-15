package dev.codesoapbox.backity.testing.http.config;

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
import dev.codesoapbox.backity.testing.TemporaryMockBean;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Import;

/**
 * <h1>Motivation for the class</h1>
 * <p>
 * While creating an application context for controller tests does not take a long time, it can lead to the context
 * cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all controller tests share a single application context should protect against cache eviction slowing
 * down the tests.
 */
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
@TemporaryMockBean(EntityManager.class)
@TemporaryMockBean(GameFileRepository.class)
@TemporaryMockBean(FileDiscoveryService.class)
@TemporaryMockBean(LogService.class)
@TemporaryMockBean(GogAuthService.class)
@TemporaryMockBean(GogEmbedClient.class)
@TemporaryMockBean(GetGamesWithFilesUseCase.class)
@TemporaryMockBean(StartFileDiscoveryUseCase.class)
@TemporaryMockBean(StopFileDiscoveryUseCase.class)
@TemporaryMockBean(GetFileDiscoveryStatusListUseCase.class)
@TemporaryMockBean(EnqueueFileUseCase.class)
@TemporaryMockBean(DeleteFileUseCase.class)
@TemporaryMockBean(DownloadFileUseCase.class)
@TemporaryMockBean(GetCurrentlyDownloadingFileUseCase.class)
@TemporaryMockBean(GetDiscoveredFileListUseCase.class)
@TemporaryMockBean(GetEnqueuedFileListUseCase.class)
@TemporaryMockBean(GetProcessedFileListUseCase.class)
@TemporaryMockBean(AuthenticateGogUseCase.class)
@TemporaryMockBean(CheckGogAuthenticationUseCase.class)
@TemporaryMockBean(RefreshGogAccessTokenUseCase.class)
@TemporaryMockBean(GetGogLibrarySizeUseCase.class)
@TemporaryMockBean(GetGogGameDetailsUseCase.class)
@TemporaryMockBean(GetLogsUseCase.class)
public class SharedControllerTestConfig {
}