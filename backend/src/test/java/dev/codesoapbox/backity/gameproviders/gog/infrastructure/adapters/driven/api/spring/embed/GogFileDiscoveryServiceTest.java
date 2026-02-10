package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.FakeGameDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.application.GameDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestDiscoveredFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameWithFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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

    private FakeGameDiscoveryProgressTracker aGameDiscoveryProgressTracker() {
        return new FakeGameDiscoveryProgressTracker();
    }

    @SuppressWarnings("UnusedReturnValue")
    class GogLibraryTestSetup {

        private final Map<String, GogGameWithFiles> gogGamesById = new HashMap<>();
        private Runnable onInteractionRunnable;

        public GogLibraryTestSetup() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(_ -> {
                        if (this.onInteractionRunnable != null) {
                            this.onInteractionRunnable.run();
                        }
                        return gogGamesById.keySet().stream().toList();
                    });
            lenient().when(gogEmbedWebClient.getGameDetails(any()))
                    .thenAnswer(inv -> {
                        String gameId = inv.getArgument(0);
                        return Optional.ofNullable(gogGamesById.get(gameId));
                    });
        }

        public GogLibraryTestSetup withGame(DiscoveredFile file) {
            gogGamesById.put("gameId" + gogGamesById.size() + 1, TestGogGameWithFiles.fromSingleFile(file));
            return this;
        }

        public void withGameMissingDetails() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(_ -> {
                        if (this.onInteractionRunnable != null) {
                            this.onInteractionRunnable.run();
                        }
                        return List.of("gameId1");
                    });
            when(gogEmbedWebClient.getGameDetails(any()))
                    .thenReturn(Optional.empty());
        }

        public GogLibraryTestSetup onInteraction(Runnable runnable) {
            this.onInteractionRunnable = runnable;
            return this;
        }
    }

    @Nested
    class GetGameProviderId {

        @Test
        void shouldReturnGameProviderId() {
            assertThat(gogFileDiscoveryService.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
        }
    }

    @Nested
    class DiscoveringFiles {

        @Test
        void shouldEmitEachDiscoveredFile() {
            DiscoveredFile gogDiscoveredFile = TestDiscoveredFile.minimalGog();
            mockGogGameLibrary()
                    .withGame(gogDiscoveredFile);
            List<DiscoveredFile> discoveredFiles = new ArrayList<>();
            GameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(discoveredFiles::add, progressTracker);

            List<DiscoveredFile> expectedDiscoveredFiles = List.of(gogDiscoveredFile);
            assertThat(discoveredFiles).isEqualTo(expectedDiscoveredFiles);
        }

        @Test
        void shouldSkipGamesWithoutDetails() {
            mockGogGameLibrary()
                    .withGameMissingDetails();
            List<DiscoveredFile> discoveredFiles = new ArrayList<>();
            GameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(discoveredFiles::add, progressTracker);

            assertThat(discoveredFiles).isEmpty();
        }
    }

    @Nested
    class StoppingDiscovery {

        @Test
        void shouldStopFileDiscoveryBeforeProcessingFiles() {
            mockGogGameLibrary()
                    .withGame(TestDiscoveredFile.minimalGog())
                    .onInteraction(() -> gogFileDiscoveryService.stopFileDiscovery());
            List<DiscoveredFile> discoveredFiles = new ArrayList<>();
            GameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(discoveredFiles::add, progressTracker);

            assertThat(discoveredFiles).isEmpty();
            verify(gogEmbedWebClient, never()).getGameDetails(any());
        }

        @Test
        void shouldStopFileDiscoveryAfterSomeFilesAlreadyDiscovered() {
            mockGogGameLibrary()
                    .withGame(TestDiscoveredFile.minimalGog())
                    .withGame(TestDiscoveredFile.minimalGog());
            List<DiscoveredFile> discoveredFiles = new ArrayList<>();
            GameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(discoveredFile -> {
                discoveredFiles.add(discoveredFile);

                // Stop discovery immediately after processing the first game's file
                gogFileDiscoveryService.stopFileDiscovery();
            }, progressTracker);

            assertThat(discoveredFiles).hasSize(1);
            verify(gogEmbedWebClient, times(1)).getGameDetails(any());
        }
    }

    @Nested
    class RestartingDiscovery {

        @Test
        void shouldRestartDiscoveryFromBeginningAfterStop() {
            mockGogGameLibrary()
                    .withGame(TestDiscoveredFile.minimalGog())
                    .withGame(TestDiscoveredFile.minimalGog());
            List<DiscoveredFile> discoveredFiles = new ArrayList<>();
            GameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(discoveredFile -> {
                discoveredFiles.add(discoveredFile);

                // Stop discovery immediately after processing the first game's file
                gogFileDiscoveryService.stopFileDiscovery();
            }, progressTracker);
            gogFileDiscoveryService.discoverAllFiles(discoveredFiles::add, progressTracker);

            assertThat(discoveredFiles).hasSize(3);
            verify(gogEmbedWebClient, times(3)).getGameDetails(any());
        }
    }

    @Nested
    class ProgressReporting {

        @Test
        void shouldEmitProgressUpdatesAsEachGameIsProcessed() {
            mockGogGameLibrary()
                    .withGame(TestDiscoveredFile.minimalGog())
                    .withGame(TestDiscoveredFile.minimalGog())
                    .withGame(TestDiscoveredFile.minimalGog());
            FakeGameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(_ -> {
                // We don't care about the DiscoveredFiles for this
            }, progressTracker);

            assertThat(progressTracker.getHistoricalDiscoveredGamesCount())
                    .isEqualTo(List.of(1, 1, 1));
        }

        /*
         * While skipping file processing suggests we haven't really discovered a new game,
         * the progress tracker depends on the processed elements count eventually reaching total count.
         * Not updating the progress tracker in this case would make it never reach 100%.
         */
        @Test
        void shouldEmitProgressUpdatesForGamesWithoutDetails() {
            mockGogGameLibrary()
                    .withGameMissingDetails();
            FakeGameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(_ -> {
                // We don't care about the DiscoveredFiles for this
            }, progressTracker);

            assertThat(progressTracker.getHistoricalDiscoveredGamesCount())
                    .isEqualTo(List.of(1));
        }

        @Test
        void shouldResetProgressBetweenEachRun() {
            mockGogGameLibrary()
                    .withGame(TestDiscoveredFile.minimalGog())
                    .withGame(TestDiscoveredFile.minimalGog());
            FakeGameDiscoveryProgressTracker progressTracker = aGameDiscoveryProgressTracker();

            gogFileDiscoveryService.discoverAllFiles(_ -> {
                // We don't care about the DiscoveredFiles for this
            }, progressTracker);
            gogFileDiscoveryService.discoverAllFiles(_ -> {
                // We don't care about the DiscoveredFiles for this
            }, progressTracker);

            assertThat(progressTracker.getGamesDiscovered()).isEqualTo(2);
        }
    }
}