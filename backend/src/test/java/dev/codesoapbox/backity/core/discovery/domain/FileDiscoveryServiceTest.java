package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
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
class FileDiscoveryServiceTest {

    private FileDiscoveryService fileDiscoveryService;

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
        fileDiscoveryService = new FileDiscoveryService(singletonList(gameProviderFileDiscoveryService),
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
                .until(() -> !fileDiscoveryService.getStatuses().getFirst().isInProgress());
    }

    @Test
    void shouldStartFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertThat(fileDiscoveryService.getStatuses().getFirst().isInProgress()).isTrue();
    }

    @Test
    void completedFileDiscoveryHandlerShouldChangeStatusOnFailure() {
        FileDiscoveryService.CompletedFileDiscoveryHandler handler =
                fileDiscoveryService.getCompletedFileDiscoveryHandler();

        handler.handle(gameProviderFileDiscoveryService).accept(null, new RuntimeException("test exception"));

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertThat(fileDiscoveryService.getStatuses().getFirst().isInProgress()).isFalse();
    }

    @Test
    void startFileDiscoveryShouldNotSaveGameInformationGivenGameAlreadyExists() {
        var gameTitle = "someGameTitle";
        var discoveredFile = aDiscoveredFile(gameTitle);
        var game = new Game(GameId.newInstance(), gameTitle);
        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.of(game));

        fileDiscoveryService = new FileDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);
        verify(gameRepository, never()).save(any());
    }

    private void waitForGameProviderFileDiscoveryToBeTriggered() {
        await().atMost(2, TimeUnit.SECONDS)
                .until(gameProviderFileDiscoveryService::hasBeenTriggered);
    }

    private GameProviderFile aDiscoveredFile(String gameTitle) {
        return new GameProviderFile(
                new GameProviderId("someGameProviderId"), gameTitle, "someTitle", "someVersion", "someUrl",
                "someOriginalFileName", "100 KB");
    }

    @Test
    void startFileDiscoveryShouldSaveGameInformationGivenItDoesNotYetExist() {
        var gameTitle = "someGameTitle";
        var discoveredFile = aDiscoveredFile(gameTitle);
        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.empty());

        fileDiscoveryService = new FileDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture());
        assertThat(gameCaptor.getValue().getTitle()).isEqualTo(gameTitle);
    }

    @Test
    void startFileDiscoveryShouldSaveDiscoveredFilesAndPublishEvents() {
        var gameTitle = "someGameTitle";
        var discoveredFile = aDiscoveredFile(gameTitle);
        var game = new Game(GameId.newInstance(), gameTitle);

        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.of(game));

        when(fileRepository.existsByUrlAndVersion(discoveredFile.url(), discoveredFile.version()))
                .thenReturn(false);

        List<FileDiscoveryProgressChangedEvent> progressUpdates = trackProgressUpdateEvents();
        fileDiscoveryService = new FileDiscoveryService(singletonList(gameProviderFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        var gameFileArgumentCaptor = ArgumentCaptor.forClass(GameFile.class);
        verify(fileRepository).save(gameFileArgumentCaptor.capture());
        GameFile savedGameFile = gameFileArgumentCaptor.getValue();
        GameFile expectedGameFile = discoveredFile.associateWith(game);
        expectedGameFile.setId(savedGameFile.getId());
        verify(eventPublisher).publish(FileDiscoveredEvent.from(expectedGameFile));
        assertThat(progressUpdates.size()).isOne();
        finishFileDiscovery();
        verify(eventPublisher, times(2)).publish(any(FileDiscoveryStatusChangedEvent.class));
    }

    private List<FileDiscoveryProgressChangedEvent> trackProgressUpdateEvents() {
        List<FileDiscoveryProgressChangedEvent> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(eventPublisher).publish(any(FileDiscoveryProgressChangedEvent.class));
        return progressList;
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        GameProviderFile gameProviderFile = aDiscoveredFile("someGameTitle");

        when(fileRepository.existsByUrlAndVersion(gameProviderFile.url(), gameProviderFile.version()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        waitForGameProviderFileDiscoveryToBeTriggered();
        gameProviderFileDiscoveryService.simulateFileDiscovery(gameProviderFile);

        verify(fileRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(FileDiscoveredEvent.class));
    }

    @Test
    void startFileDiscoveryShouldSetGameProviderIdServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.startFileDiscovery();

        gameProviderFileDiscoveryService.complete();

        waitForFileDiscoveryToStop();
        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
    }

    @Test
    void startFileDiscoveryShouldNotTriggerWhenGameProviderIdDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.startFileDiscovery();
        gameProviderFileDiscoveryService.complete();
        waitForFileDiscoveryToStop();

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertThat(gameProviderFileDiscoveryService.getTimesTriggered().get()).isOne();
    }

    @Test
    void shouldStopFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.stopFileDiscovery();

        waitForFileDiscoveryToStop();
        assertThat(gameProviderFileDiscoveryService.getStoppedTimes()).isOne();
    }

    @Test
    void shouldNotStopFileDiscoveryIfAlreadyStopped() {
        fileDiscoveryService.stopFileDiscovery();
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
        public void startFileDiscovery(Consumer<GameProviderFile> fileConsumer) {
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