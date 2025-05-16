package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameProviderFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameWithFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogFileDiscoveryServiceTest {

    private final GogGameWithFilesMapper gogGameWithFilesMapper = Mappers.getMapper(GogGameWithFilesMapper.class);

    private GogFileDiscoveryService gogFileDiscoveryService;

    @Mock
    private GogEmbedWebClient gogEmbedWebClient;

    @BeforeEach
    void setUp() {
        gogFileDiscoveryService = new GogFileDiscoveryService(gogEmbedWebClient, gogGameWithFilesMapper);
    }

    private GogLibraryTestSetup mockGogGameLibrary() {
        return new GogLibraryTestSetup();
    }

    @Test
    void shouldGetSource() {
        assertThat(gogFileDiscoveryService.getGameProviderId()).isEqualTo("GOG");
    }

    class GogLibraryTestSetup {

        private final Map<String, GogGameWithFiles> gogGamesById = new HashMap<>();
        private Runnable onInteractionRunnable;

        public GogLibraryTestSetup() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(inv -> {
                        if(this.onInteractionRunnable != null) {
                            this.onInteractionRunnable.run();
                        }
                        return gogGamesById.keySet().stream().toList();
                    });
            lenient().when(gogEmbedWebClient.getGameDetails(any()))
                    .thenAnswer(inv -> {
                        String gameId = inv.getArgument(0);
                        return gogGamesById.get(gameId);
                    });
        }

        GogLibraryTestSetup withGame(GameProviderFile file) {
            gogGamesById.put("gameId" + gogGamesById.size() + 1, TestGogGameWithFiles.fromSingleFile(file));
            return this;
        }

        public void withGameMissingDetails() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(inv -> {
                        if(this.onInteractionRunnable != null) {
                            this.onInteractionRunnable.run();
                        }
                        return List.of("gameId1");
                    });
            when(gogEmbedWebClient.getGameDetails(any()))
                    .thenReturn(null);
        }

        @SuppressWarnings("UnusedReturnValue")
        public GogLibraryTestSetup onInteraction(Runnable runnable) {
            this.onInteractionRunnable = runnable;
            return this;
        }
    }

    @Nested
    class DiscoveringFiles {

        @Test
        void startFileDiscoveryShouldDiscoverNewFiles() {
            GameProviderFile gogGameProviderFile = TestGameProviderFile.minimalGog();
            mockGogGameLibrary()
                    .withGame(gogGameProviderFile);
            List<GameProviderFile> discoveredGameProviderFiles = new ArrayList<>();

            gogFileDiscoveryService.startFileDiscovery(discoveredGameProviderFiles::add);

            List<GameProviderFile> expectedGameProviderFiles = List.of(gogGameProviderFile);
            assertThat(discoveredGameProviderFiles).isEqualTo(expectedGameProviderFiles);
        }

        @Test
        void startFileDiscoveryShouldIgnoreGamesWithoutDetails() {
            mockGogGameLibrary()
                    .withGameMissingDetails();
            List<GameProviderFile> discoveredGameProviderFiles = new ArrayList<>();

            gogFileDiscoveryService.startFileDiscovery(discoveredGameProviderFiles::add);

            assertThat(discoveredGameProviderFiles).isEmpty();
        }
    }

    @Nested
    class StoppingDiscovery {

        @Test
        void startFileDiscoveryShouldRestartFileDiscoveryAfterStopping() {
            mockGogGameLibrary()
                    .withGame(TestGameProviderFile.minimalGog());
            var discoveredFilesCount = new AtomicInteger();

            gogFileDiscoveryService.stopFileDiscovery();
            gogFileDiscoveryService.startFileDiscovery(gf -> discoveredFilesCount.getAndIncrement());

            assertThat(discoveredFilesCount.get()).isEqualTo(1);
        }

        @Test
        void shouldStopFileDiscoveryBeforeProcessingFiles() {
            mockGogGameLibrary()
                    .withGame(TestGameProviderFile.minimalGog())
                    .onInteraction(() -> gogFileDiscoveryService.stopFileDiscovery());
            List<GameProviderFile> discoveredGameProviderFiles = new ArrayList<>();

            gogFileDiscoveryService.startFileDiscovery(discoveredGameProviderFiles::add);

            assertThat(discoveredGameProviderFiles).isEmpty();
            verify(gogEmbedWebClient, never()).getGameDetails(any());
        }

        @Test
        void shouldStopInProgressDiscovery() {
            mockGogGameLibrary()
                    .withGame(TestGameProviderFile.minimalGog())
                    .withGame(TestGameProviderFile.minimalGog());
            List<GameProviderFile> discoveredGameProviderFiles = new ArrayList<>();

            gogFileDiscoveryService.startFileDiscovery(e -> {
                discoveredGameProviderFiles.add(e);

                // Stop discovery immediately after processing the first game's file
                gogFileDiscoveryService.stopFileDiscovery();
            });

            assertThat(discoveredGameProviderFiles).hasSize(1);
            verify(gogEmbedWebClient, times(1)).getGameDetails(any());
        }
    }

    @Nested
    class ProgressReporting {

        @Test
        void shouldSubscribeToProgress() {
            mockGogGameLibrary()
                    .withGame(TestGameProviderFile.minimalGog())
                    .withGame(TestGameProviderFile.minimalGog())
                    .withGame(TestGameProviderFile.minimalGog());
            List<ProgressInfo> progressInfos = new ArrayList<>();

            gogFileDiscoveryService.subscribeToProgress(progressInfos::add);
            gogFileDiscoveryService.startFileDiscovery(df -> {
                // We don't care about the GameProviderFiles for this
            });

            assertThat(progressInfos.get(0).percentage()).isEqualTo(33);
            assertThat(progressInfos.get(1).percentage()).isEqualTo(66);
            assertThat(progressInfos.get(2).percentage()).isEqualTo(100);
        }

        @Test
        void shouldGetProgressWhenNoProgress() {
            assertThat(gogFileDiscoveryService.getProgress()).isEqualTo(ProgressInfo.none());
        }

        @Test
        void getProgressShouldReturnCurrentProgress() {
            mockGogGameLibrary()
                    .withGame(TestGameProviderFile.minimalGog());
            gogFileDiscoveryService.startFileDiscovery(df -> {
                // We don't care about the GameProviderFiles for this
            });

            ProgressInfo progressInfo = gogFileDiscoveryService.getProgress();

            assertThat(progressInfo.percentage()).isEqualTo(100);
        }
    }
}