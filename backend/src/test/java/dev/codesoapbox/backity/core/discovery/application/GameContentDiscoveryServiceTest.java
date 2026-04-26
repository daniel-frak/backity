package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.core.sourcefile.domain.TestDiscoveredFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryServiceTest {

    private GameContentDiscoveryService gameContentDiscoveryService;

    private FakeGameProviderFileDiscoveryService gameProviderFileDiscoveryService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SourceFileRepository fileRepository;

    @Mock
    private GameContentDiscoveryProgressTracker discoveryProgressTracker;

    private boolean discoveryIsInProgress = false;

    @BeforeEach
    void setUp() {
        gameProviderFileDiscoveryService = new FakeGameProviderFileDiscoveryService();
        gameContentDiscoveryService = new GameContentDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, discoveryProgressTracker, Executors.newVirtualThreadPerTaskExecutor());

        mockDiscoveryTrackerTracksInProgressStatus();
    }

    private void mockDiscoveryTrackerTracksInProgressStatus() {
        lenient().doAnswer(inv -> {
                    discoveryIsInProgress = true;
                    return null;
                }).when(discoveryProgressTracker)
                .initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        lenient().doAnswer(inv -> {
                    discoveryIsInProgress = false;
                    return null;
                }).when(discoveryProgressTracker)
                .finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        lenient().when(discoveryProgressTracker.isInProgress(eq(gameProviderFileDiscoveryService)))
                .thenAnswer(inv -> discoveryIsInProgress);
    }

    @AfterEach
    void tearDown() {
        // Manually finish file discovery to prevent thread starvation
        finishFileDiscovery();
    }

    private void finishFileDiscovery() {
        gameProviderFileDiscoveryService.finishLatch.countDown();
        waitForFileDiscoveryToStop();
    }

    private void waitForFileDiscoveryToStop() {
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !discoveryIsInProgress);
    }

    @Nested
    class StartingDiscovery {

        @Test
        void shouldStartContentDiscovery() {
            gameContentDiscoveryService.startContentDiscovery();

            assertThat(discoveryIsInProgress).isTrue();
        }

        @Test
        void startContentDiscoveryShouldInitializeTrackingOnStart() {
            gameContentDiscoveryService.startContentDiscovery();

            verify(discoveryProgressTracker).initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        }

        @Test
        void startContentDiscoveryShouldSetStoppedAtNowOnFinish() {
            gameContentDiscoveryService.startContentDiscovery();
            finishFileDiscovery();

            verify(discoveryProgressTracker).finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        }

        @Test
        void startContentDiscoveryShouldChangeStatusToNotInProgressOnSuccess() {
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            finishFileDiscovery();

            assertThat(discoveryIsInProgress).isFalse();
        }

        private void waitForGameProviderFileDiscoveryToBeTriggered() {
            await().atMost(2, TimeUnit.SECONDS)
                    .until(gameProviderFileDiscoveryService::hasBeenTriggered);
        }

        @Test
        void startContentDiscoveryShouldChangeStatusToNotInProgressOnFailure() {
            gameProviderFileDiscoveryService.setExceptionToThrowDuringDiscovery(new RuntimeException("Test exception"));
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();

            assertThat(discoveryIsInProgress).isFalse();
        }

        @Test
        void startContentDiscoveryShouldFinalizeTrackingOnSuccess() {
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            finishFileDiscovery();

            verify(discoveryProgressTracker).finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        }

        @Test
        void startContentDiscoveryShouldFinalizeTrackingOnFailure() {
            gameProviderFileDiscoveryService.setExceptionToThrowDuringDiscovery(new RuntimeException("Test exception"));
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();

            verify(discoveryProgressTracker).finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());
        }

        @Test
        void startContentDiscoveryShouldMarkSuccessfulOnSuccess() {
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            finishFileDiscovery();

            verify(discoveryProgressTracker).markSuccessful(gameProviderFileDiscoveryService.getGameProviderId());
        }

        @Test
        void startContentDiscoveryShouldNotMarkSuccessfulOnFailure() {
            gameProviderFileDiscoveryService.setExceptionToThrowDuringDiscovery(new RuntimeException("Test exception"));
            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();

            verify(discoveryProgressTracker, never()).markSuccessful(any());
        }

        @Test
        void startContentDiscoveryShouldNotSaveGameInformationGivenGameAlreadyExists() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            mockGameExists(discoveredFile.originalGameTitle());

            gameContentDiscoveryService =
                    new GameContentDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                            gameRepository, fileRepository, discoveryProgressTracker,
                            Executors.newVirtualThreadPerTaskExecutor());

            gameContentDiscoveryService.startContentDiscovery();

            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);
            verify(gameRepository, never()).save(any());
        }

        private Game mockGameExists(String title) {
            Game game = TestGame.anyBuilder()
                    .withTitle(title)
                    .build();
            when(gameRepository.findByTitle(game.getTitle()))
                    .thenReturn(Optional.of(game));

            return game;
        }

        @Test
        void startContentDiscoveryShouldSaveGameInformationGivenItDoesNotYetExist() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            mockGameDoesNotExist(discoveredFile.originalGameTitle());

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

            Game savedGame = getSavedGame();
            assertThat(savedGame.getTitle()).isEqualTo(discoveredFile.originalGameTitle());
        }

        private void mockGameDoesNotExist(String gameTitle) {
            when(gameRepository.findByTitle(gameTitle))
                    .thenReturn(Optional.empty());
        }

        private Game getSavedGame() {
            ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
            verify(gameRepository).save(gameCaptor.capture());
            return gameCaptor.getValue();
        }

        @Test
        void startContentDiscoveryShouldSaveSourceFiles() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            Game game = mockGameExists(discoveredFile.originalGameTitle());
            mockSourceFileDoesNotExist(discoveredFile);

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

            SourceFile savedSourceFile = getSavedSourceFile();
            SourceFile expectedSourceFile = getExpectedSourceFile(game, discoveredFile, savedSourceFile);
            assertThat(savedSourceFile).usingRecursiveComparison()
                    .isEqualTo(expectedSourceFile);
        }

        private SourceFile getExpectedSourceFile(Game game, DiscoveredFile discoveredFile, SourceFile savedSourceFile) {
            SourceFile expectedSourceFile = SourceFile.createFor(game, discoveredFile);
            expectedSourceFile.setId(savedSourceFile.getId());
            return expectedSourceFile;
        }

        private SourceFile getSavedSourceFile() {
            var sourceFileArgumentCaptor = ArgumentCaptor.forClass(SourceFile.class);
            verify(fileRepository).save(sourceFileArgumentCaptor.capture());
            return sourceFileArgumentCaptor.getValue();
        }

        private void mockSourceFileDoesNotExist(DiscoveredFile discoveredFile) {
            when(fileRepository.existsByUrlAndVersion(discoveredFile.url(), discoveredFile.version()))
                    .thenReturn(false);
        }

        @Test
        void startContentDiscoveryShouldIncrementSourceFilesDiscovered() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            mockGameExists(discoveredFile.originalGameTitle());
            mockSourceFileDoesNotExist(discoveredFile);

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

            verify(discoveryProgressTracker).incrementSourceFilesDiscovered(
                    gameProviderFileDiscoveryService.getGameProviderId(), 1);
        }

        @Test
        void startContentDiscoveryShouldIncrementGamesDiscovered() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            mockGameDoesNotExist(discoveredFile.originalGameTitle());

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

            verify(discoveryProgressTracker).incrementGamesDiscovered(
                    gameProviderFileDiscoveryService.getGameProviderId(), 1);
        }

        @Test
        void startContentDiscoveryShouldNotSaveSourceFileIfAlreadyExists() {
            DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();
            mockSourceFileExistsLocally(discoveredFile);

            gameContentDiscoveryService.startContentDiscovery();

            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

            verify(fileRepository, never()).save(any());
            verify(discoveryProgressTracker, never()).incrementSourceFilesDiscovered(any(), anyInt());
        }

        private void mockSourceFileExistsLocally(DiscoveredFile discoveredFile) {
            when(fileRepository.existsByUrlAndVersion(discoveredFile.url(), discoveredFile.version()))
                    .thenReturn(true);
        }

        @Test
        void startContentDiscoveryShouldSetGameProviderIdServiceAsNotInProgressGivenDone() {
            gameContentDiscoveryService.startContentDiscovery();

            gameProviderFileDiscoveryService.complete();

            waitForFileDiscoveryToStop();
            assertThat(discoveryIsInProgress).isFalse();
        }

        @Test
        void startContentDiscoveryServiceShouldNotTriggerDiscoveryGivenAlreadyInProgress() {
            gameContentDiscoveryService.startContentDiscovery();
            gameContentDiscoveryService.startContentDiscovery();
            gameProviderFileDiscoveryService.complete();
            waitForFileDiscoveryToStop();

            assertThat(gameProviderFileDiscoveryService.getTimesTriggered().get()).isOne();
        }
    }

    @Nested
    class StoppingDiscovery {

        @Test
        void shouldStopContentDiscovery() {
            gameContentDiscoveryService.startContentDiscovery();
            gameContentDiscoveryService.stopContentDiscovery();

            waitForFileDiscoveryToStop();
            assertThat(gameProviderFileDiscoveryService.getStoppedTimes()).isOne();
        }

        @Test
        void shouldNotStopContentDiscoveryIfAlreadyStopped() {
            gameContentDiscoveryService.stopContentDiscovery();
            assertThat(gameProviderFileDiscoveryService.getStoppedTimes()).isZero();
        }
    }
}
