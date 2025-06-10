package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
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
    void shouldReturnGameProviderId() {
        assertThat(gogFileDiscoveryService.getGameProviderId()).isEqualTo(new GameProviderId("GOG"));
    }

    @SuppressWarnings("UnusedReturnValue")
    class GogLibraryTestSetup {

        private final Map<String, GogGameWithFiles> gogGamesById = new HashMap<>();
        private Runnable onInteractionRunnable;

        public GogLibraryTestSetup() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(inv -> {
                        if (this.onInteractionRunnable != null) {
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

        public GogLibraryTestSetup withGame(FileSource file) {
            gogGamesById.put("gameId" + gogGamesById.size() + 1, TestGogGameWithFiles.fromSingleFile(file));
            return this;
        }

        public void withGameMissingDetails() {
            when(gogEmbedWebClient.getLibraryGameIds())
                    .thenAnswer(inv -> {
                        if (this.onInteractionRunnable != null) {
                            this.onInteractionRunnable.run();
                        }
                        return List.of("gameId1");
                    });
            when(gogEmbedWebClient.getGameDetails(any()))
                    .thenReturn(null);
        }

        public GogLibraryTestSetup onInteraction(Runnable runnable) {
            this.onInteractionRunnable = runnable;
            return this;
        }
    }

    @Nested
    class DiscoveringFiles {

        @Test
        void shouldEmitEachDiscoveredFile() {
            FileSource gogFileSource = TestFileSource.minimalGog();
            mockGogGameLibrary()
                    .withGame(gogFileSource);
            List<FileSource> discoveredFileSources = new ArrayList<>();

            gogFileDiscoveryService.discoverAllFiles(discoveredFileSources::add);

            List<FileSource> expectedFileSources = List.of(gogFileSource);
            assertThat(discoveredFileSources).isEqualTo(expectedFileSources);
        }

        @Test
        void shouldSkipGamesWithoutDetails() {
            mockGogGameLibrary()
                    .withGameMissingDetails();
            List<FileSource> discoveredFileSources = new ArrayList<>();

            gogFileDiscoveryService.discoverAllFiles(discoveredFileSources::add);

            assertThat(discoveredFileSources).isEmpty();
        }
    }

    @Nested
    class StoppingDiscovery {

        @Test
        void shouldStopFileDiscoveryBeforeProcessingFiles() {
            mockGogGameLibrary()
                    .withGame(TestFileSource.minimalGog())
                    .onInteraction(() -> gogFileDiscoveryService.stopFileDiscovery());
            List<FileSource> discoveredFileSources = new ArrayList<>();

            gogFileDiscoveryService.discoverAllFiles(discoveredFileSources::add);

            assertThat(discoveredFileSources).isEmpty();
            verify(gogEmbedWebClient, never()).getGameDetails(any());
        }

        @Test
        void shouldStopFileDiscoveryAfterSomeFilesAlreadyDiscovered() {
            mockGogGameLibrary()
                    .withGame(TestFileSource.minimalGog())
                    .withGame(TestFileSource.minimalGog());
            List<FileSource> discoveredFileSources = new ArrayList<>();

            gogFileDiscoveryService.discoverAllFiles(fileSource -> {
                discoveredFileSources.add(fileSource);

                // Stop discovery immediately after processing the first game's file
                gogFileDiscoveryService.stopFileDiscovery();
            });

            assertThat(discoveredFileSources).hasSize(1);
            verify(gogEmbedWebClient, times(1)).getGameDetails(any());
        }
    }

    @Nested
    class RestartingDiscovery {

        @Test
        void shouldRestartDiscoveryFromBeginningAfterStop() {
            mockGogGameLibrary()
                    .withGame(TestFileSource.minimalGog())
                    .withGame(TestFileSource.minimalGog());
            List<FileSource> discoveredFileSources = new ArrayList<>();

            gogFileDiscoveryService.discoverAllFiles(fileSource -> {
                discoveredFileSources.add(fileSource);

                // Stop discovery immediately after processing the first game's file
                gogFileDiscoveryService.stopFileDiscovery();
            });
            gogFileDiscoveryService.discoverAllFiles(discoveredFileSources::add);

            assertThat(discoveredFileSources).hasSize(3);
            verify(gogEmbedWebClient, times(3)).getGameDetails(any());
        }
    }

    @Nested
    class ProgressReporting {

        @Test
        void shouldEmitProgressUpdatesAsEachGameIsProcessed() {
            mockGogGameLibrary()
                    .withGame(TestFileSource.minimalGog())
                    .withGame(TestFileSource.minimalGog())
                    .withGame(TestFileSource.minimalGog());
            List<ProgressInfo> progressInfos = new ArrayList<>();

            gogFileDiscoveryService.subscribeToProgress(progressInfos::add);
            gogFileDiscoveryService.discoverAllFiles(fileSource -> {
                // We don't care about the FileSources for this
            });

            assertThat(progressInfos.get(0).percentage()).isEqualTo(33);
            assertThat(progressInfos.get(1).percentage()).isEqualTo(66);
            assertThat(progressInfos.get(2).percentage()).isEqualTo(100);
        }

        @Test
        void shouldReturnNoneWhenNoDiscoveryHasStarted() {
            assertThat(gogFileDiscoveryService.getProgressInfo())
                    .isEqualTo(ProgressInfo.none());
        }

        @Test
        void shouldReturnCurrentProgressAfterDiscovery() {
            mockGogGameLibrary()
                    .withGame(TestFileSource.minimalGog());
            gogFileDiscoveryService.discoverAllFiles(fileSource -> {
                // We don't care about the FileSources for this
            });

            ProgressInfo progressInfo = gogFileDiscoveryService.getProgressInfo();

            assertThat(progressInfo.percentage()).isEqualTo(100);
        }
    }
}