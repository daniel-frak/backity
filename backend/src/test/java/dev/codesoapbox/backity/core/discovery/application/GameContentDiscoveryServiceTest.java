package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    private DomainEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        gameProviderFileDiscoveryService = new FakeGameProviderFileDiscoveryService();
        gameContentDiscoveryService = new GameContentDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);
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
                .until(() -> !gameContentDiscoveryService.getStatuses().getFirst().isInProgress());
    }

    @Test
    void shouldStartContentDiscovery() {
        gameContentDiscoveryService.startContentDiscovery();

        assertThat(gameContentDiscoveryService.getStatuses().size()).isOne();
        assertThat(gameContentDiscoveryService.getStatuses().getFirst().isInProgress()).isTrue();
    }

    @Test
    void completedGameContentDiscoveryHandlerShouldChangeStatusOnFailure() {
        GameContentDiscoveryService.CompletedGameContentDiscoveryHandler handler =
                gameContentDiscoveryService.getCompletedGameContentDiscoveryHandler();

        handler.handle(gameProviderFileDiscoveryService).accept(null, new RuntimeException("test exception"));

        assertThat(gameContentDiscoveryService.getStatuses().size()).isOne();
        assertThat(gameContentDiscoveryService.getStatuses().getFirst().isInProgress()).isFalse();
    }

    @Test
    void startContentDiscoveryShouldNotSaveGameInformationGivenGameAlreadyExists() {
        GameProviderFile discoveredFile = TestGameFile.discovered().getGameProviderFile();
        mockGameExists(discoveredFile.originalGameTitle());

        gameContentDiscoveryService = new GameContentDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

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

    private void waitForGameProviderFileDiscoveryToBeTriggered() {
        await().atMost(2, TimeUnit.SECONDS)
                .until(gameProviderFileDiscoveryService::hasBeenTriggered);
    }

    @Test
    void startContentDiscoveryShouldSaveGameInformationGivenItDoesNotYetExist() {
        GameProviderFile discoveredFile = TestGameFile.discovered().getGameProviderFile();
        mockGameDoesNotExist(discoveredFile.originalGameTitle());

        gameContentDiscoveryService.startContentDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture());
        assertThat(gameCaptor.getValue().getTitle()).isEqualTo(discoveredFile.originalGameTitle());
    }

    private void mockGameDoesNotExist(String gameTitle) {
        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.empty());
    }

    @Test
    void startContentDiscoveryShouldSaveDiscoveredFilesAndPublishEvents() {
        GameProviderFile discoveredFile = TestGameFile.discovered().getGameProviderFile();
        Game game = mockGameExists(discoveredFile.originalGameTitle());
        mockDoesNotExistLocally(discoveredFile);

        List<GameContentDiscoveryProgressChangedEvent> progressUpdates = trackProgressUpdateEvents();
        gameContentDiscoveryService = new GameContentDiscoveryService(List.of(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        gameContentDiscoveryService.startContentDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        var gameFileArgumentCaptor = ArgumentCaptor.forClass(GameFile.class);
        verify(fileRepository).save(gameFileArgumentCaptor.capture());
        GameFile savedGameFile = gameFileArgumentCaptor.getValue();
        GameFile expectedGameFile = GameFile.associate(game, discoveredFile);
        expectedGameFile.setId(savedGameFile.getId());
        verify(eventPublisher).publish(FileDiscoveredEvent.from(expectedGameFile));
        assertThat(progressUpdates.size()).isOne();
        finishFileDiscovery();
        verify(eventPublisher, times(2)).publish(any(GameContentDiscoveryStatusChangedEvent.class));
    }

    private void mockDoesNotExistLocally(GameProviderFile discoveredFile) {
        when(fileRepository.existsByUrlAndVersion(discoveredFile.url(), discoveredFile.version()))
                .thenReturn(false);
    }

    private List<GameContentDiscoveryProgressChangedEvent> trackProgressUpdateEvents() {
        List<GameContentDiscoveryProgressChangedEvent> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(eventPublisher).publish(any(GameContentDiscoveryProgressChangedEvent.class));
        return progressList;
    }

    @Test
    void startContentDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        GameProviderFile gameProviderFile = TestGameFile.discovered().getGameProviderFile();
        mockExistsLocally(gameProviderFile);

        gameContentDiscoveryService.startContentDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(gameProviderFile);

        verify(fileRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(FileDiscoveredEvent.class));
    }

    private void mockExistsLocally(GameProviderFile gameProviderFile) {
        when(fileRepository.existsByUrlAndVersion(gameProviderFile.url(), gameProviderFile.version()))
                .thenReturn(true);
    }

    @Test
    void startContentDiscoveryShouldSetGameProviderIdServiceAsNotInProgressWhenDone() {
        gameContentDiscoveryService.startContentDiscovery();

        gameProviderFileDiscoveryService.complete();

        waitForFileDiscoveryToStop();
        assertThat(gameContentDiscoveryService.getStatuses().size()).isOne();
    }

    @Test
    void startContentDiscoveryServiceAlreadyInProgress() {
        gameContentDiscoveryService.startContentDiscovery();
        gameContentDiscoveryService.startContentDiscovery();
        gameProviderFileDiscoveryService.complete();
        waitForFileDiscoveryToStop();

        assertThat(gameContentDiscoveryService.getStatuses().size()).isOne();
        assertThat(gameProviderFileDiscoveryService.getTimesTriggered().get()).isOne();
    }

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

    private static class FakeGameProviderFileDiscoveryService implements GameProviderFileDiscoveryService {

        private final CountDownLatch finishLatch = new CountDownLatch(1);
        private final AtomicReference<Consumer<GameProviderFile>> gameProviderFileConsumerRef =
                new AtomicReference<>();

        @Getter
        private final AtomicInteger timesTriggered = new AtomicInteger();

        @Getter
        private int stoppedTimes = 0;

        @Setter
        private RuntimeException exception;

        public boolean hasBeenTriggered() {
            return timesTriggered.get() > 0;
        }

        public void simulateFileDiscovery(GameProviderFile gameProviderFile) {
            gameProviderFileConsumerRef.get().accept(gameProviderFile);
        }

        public void complete() {
            finishLatch.countDown();
        }

        @Override
        public String getGameProviderId() {
            return "someGameProviderId";
        }

        @Override
        public void discoverAllFiles(Consumer<GameProviderFile> fileConsumer) {
            if (exception != null) {
                throw exception;
            }
            this.gameProviderFileConsumerRef.set(fileConsumer);
            timesTriggered.incrementAndGet();

            try {
                finishLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("File discovery interrupted", e);
            }

            log.info("Finished file discovery");
        }

        @Override
        public void stopFileDiscovery() {
            stoppedTimes++;
            finishLatch.countDown();
        }

        @Override
        public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
            progressConsumer.accept(getProgress());
        }

        @Override
        public ProgressInfo getProgress() {
            return new ProgressInfo(25, Duration.of(1234, ChronoUnit.SECONDS));
        }
    }
}