package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
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
    private GameFileRepository fileRepository;

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
            FileSource fileSource = TestGameFile.gog().getFileSource();
            mockGameExists(fileSource.originalGameTitle());

            gameContentDiscoveryService =
                    new GameContentDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                            gameRepository, fileRepository, discoveryProgressTracker,
                            Executors.newVirtualThreadPerTaskExecutor());

            gameContentDiscoveryService.startContentDiscovery();

            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);
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
            FileSource fileSource = TestGameFile.gog().getFileSource();
            mockGameDoesNotExist(fileSource.originalGameTitle());

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);

            Game savedGame = getSavedGame();
            assertThat(savedGame.getTitle()).isEqualTo(fileSource.originalGameTitle());
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
        void startContentDiscoveryShouldSaveGameFiles() {
            FileSource fileSource = TestGameFile.gog().getFileSource();
            Game game = mockGameExists(fileSource.originalGameTitle());
            mockGameFileDoesNotExist(fileSource);

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);

            GameFile savedGameFile = getSavedGameFile();
            GameFile expectedGameFile = getExpectedGameFile(game, fileSource, savedGameFile);
            assertThat(savedGameFile).usingRecursiveComparison()
                    .isEqualTo(expectedGameFile);
        }

        private GameFile getExpectedGameFile(Game game, FileSource fileSource, GameFile savedGameFile) {
            GameFile expectedGameFile = GameFile.createFor(game, fileSource);
            expectedGameFile.setId(savedGameFile.getId());
            return expectedGameFile;
        }

        private GameFile getSavedGameFile() {
            var gameFileArgumentCaptor = ArgumentCaptor.forClass(GameFile.class);
            verify(fileRepository).save(gameFileArgumentCaptor.capture());
            return gameFileArgumentCaptor.getValue();
        }

        private void mockGameFileDoesNotExist(FileSource fileSource) {
            when(fileRepository.existsByUrlAndVersion(fileSource.url(), fileSource.version()))
                    .thenReturn(false);
        }

        @Test
        void startContentDiscoveryShouldIncrementGameFilesDiscovered() {
            FileSource fileSource = TestGameFile.gog().getFileSource();
            mockGameExists(fileSource.originalGameTitle());
            mockGameFileDoesNotExist(fileSource);

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);

            verify(discoveryProgressTracker).incrementGameFilesDiscovered(
                    gameProviderFileDiscoveryService.getGameProviderId(), 1);
        }

        @Test
        void startContentDiscoveryShouldIncrementGamesDiscovered() {
            FileSource fileSource = TestGameFile.gog().getFileSource();
            mockGameDoesNotExist(fileSource.originalGameTitle());

            gameContentDiscoveryService.startContentDiscovery();
            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);

            verify(discoveryProgressTracker).incrementGamesDiscovered(
                    gameProviderFileDiscoveryService.getGameProviderId(), 1);
        }

        @Test
        void startContentDiscoveryShouldNotSaveGameFileIfAlreadyExists() {
            FileSource fileSource = TestGameFile.gog().getFileSource();
            mockGameFileExistsLocally(fileSource);

            gameContentDiscoveryService.startContentDiscovery();

            waitForGameProviderFileDiscoveryToBeTriggered();
            gameProviderFileDiscoveryService.simulateFileDiscovery(fileSource);

            verify(fileRepository, never()).save(any());
            verify(discoveryProgressTracker, never()).incrementGameFilesDiscovered(any(), anyInt());
        }

        private void mockGameFileExistsLocally(FileSource fileSource) {
            when(fileRepository.existsByUrlAndVersion(fileSource.url(), fileSource.version()))
                    .thenReturn(true);
        }

        @Test
        void startContentDiscoveryShouldSetGameProviderIdServiceAsNotInProgressWhenDone() {
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
