package dev.codesoapbox.backity.core.shared.config.http;

import dev.codesoapbox.backity.core.discovery.adapters.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.adapters.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.adapters.application.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.config.FileDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.game.application.GameFacade;
import dev.codesoapbox.backity.core.game.config.GameControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefiledetails.application.*;
import dev.codesoapbox.backity.core.gamefiledetails.config.GameFileDetailsControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedControllerBeanConfig;
import dev.codesoapbox.backity.integrations.gog.application.*;
import dev.codesoapbox.backity.integrations.gog.config.GogControllerBeanConfig;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
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
        TestTimeBeanConfig.class,

        // Specific
        FileDiscoveryControllerBeanConfig.class,
        GogControllerBeanConfig.class,
        GameControllerBeanConfig.class,
        GameFileDetailsControllerBeanConfig.class
})
@MockBeans({
        @MockBean(EntityManager.class),
        @MockBean(GameFileDetailsRepository.class),
        @MockBean(FileDiscoveryService.class),
        @MockBean(LogService.class),
        @MockBean(GogAuthService.class),
        @MockBean(GogEmbedClient.class),
        @MockBean(GameFacade.class),
        @MockBean(StartFileDiscoveryUseCase.class),
        @MockBean(StopFileDiscoveryUseCase.class),
        @MockBean(GetFileDiscoveryStatusListUseCase.class),
        @MockBean(EnqueueFileUseCase.class),
        @MockBean(GetCurrentlyDownloadingFileUseCase.class),
        @MockBean(GetDiscoveredFileListUseCase.class),
        @MockBean(GetEnqueuedFileListUseCase.class),
        @MockBean(GetProcessedFileListUseCase.class),
        @MockBean(AuthenticateGogUseCase.class),
        @MockBean(CheckGogAuthenticationUseCase.class),
        @MockBean(RefreshGogAccessTokenUseCase.class),
        @MockBean(GetGogLibrarySizeUseCase.class),
        @MockBean(GetGogGameDetailsUseCase.class),
        @MockBean(GetLogsUseCase.class)
})
public class SharedControllerTestConfig {
}