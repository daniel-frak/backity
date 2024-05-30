package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
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

    private FakeSourceFileDiscoveryService sourceFileDiscoveryService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private FileDetailsRepository fileRepository;

    @Mock
    private FileDiscoveryEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        sourceFileDiscoveryService = new FakeSourceFileDiscoveryService();
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);
    }

    @AfterEach
    void tearDown() {
        // Manually finish file discovery to prevent thread starvation
        finishFileDiscovery();
    }

    private void finishFileDiscovery() {
        sourceFileDiscoveryService.finishLatch.countDown();
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

        handler.handle(sourceFileDiscoveryService).accept(null, new RuntimeException("test exception"));

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

        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForSourceFileDiscoveryToBeTriggered();
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);
        verify(gameRepository, never()).save(any());
    }

    private void waitForSourceFileDiscoveryToBeTriggered() {
        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
    }

    @Test
    void startFileDiscoveryShouldSaveGameInformationGivenItDoesNotYetExist() {
        var gameTitle = "someGameTitle";
        var discoveredFile = aDiscoveredFile(gameTitle);
        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.empty());

        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForSourceFileDiscoveryToBeTriggered();
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture());
        assertThat(gameCaptor.getValue().getTitle()).isEqualTo(gameTitle);
    }

    private SourceFileDetails aDiscoveredFile(String gameTitle) {
        return new SourceFileDetails(
                new FileSourceId("someSource"), gameTitle, "someTitle", "someVersion", "someUrl",
                "someOriginalFileName", "100 KB");
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
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, eventPublisher);

        fileDiscoveryService.startFileDiscovery();

        waitForSourceFileDiscoveryToBeTriggered();
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        var fileDetailsArgumentCaptor = ArgumentCaptor.forClass(FileDetails.class);
        verify(fileRepository).save(fileDetailsArgumentCaptor.capture());
        FileDetails savedFileDetails = fileDetailsArgumentCaptor.getValue();
        FileDetails expectedFileDetails = discoveredFile.associateWith(game);
        expectedFileDetails.setId(savedFileDetails.getId());
        verify(eventPublisher).publishFileDiscoveredEvent(expectedFileDetails);
        assertThat(progressUpdates.size()).isOne();
        finishFileDiscovery();
        verify(eventPublisher, times(2)).publishStatusChangedEvent(any());
    }

    private List<FileDiscoveryProgressChangedEvent> trackProgressUpdateEvents() {
        List<FileDiscoveryProgressChangedEvent> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(eventPublisher).publishProgressChangedEvent(any());
        return progressList;
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        SourceFileDetails sourceFileDetails = aDiscoveredFile("someGameTitle");

        when(fileRepository.existsByUrlAndVersion(sourceFileDetails.url(), sourceFileDetails.version()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        waitForSourceFileDiscoveryToBeTriggered();
        sourceFileDiscoveryService.simulateFileDiscovery(sourceFileDetails);

        verify(fileRepository, never()).save(any());
        verify(eventPublisher, never()).publishFileDiscoveredEvent(any());
    }

    @Test
    void startFileDiscoveryShouldSetSourceServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.startFileDiscovery();

        sourceFileDiscoveryService.complete();

        waitForFileDiscoveryToStop();
        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
    }

    @Test
    void startFileDiscoveryShouldNotTriggerWhenSourceDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.startFileDiscovery();
        sourceFileDiscoveryService.complete();
        waitForFileDiscoveryToStop();

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertThat(sourceFileDiscoveryService.getTimesTriggered().get()).isOne();
    }

    @Test
    void shouldStopFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.stopFileDiscovery();

        waitForFileDiscoveryToStop();
        assertThat(sourceFileDiscoveryService.getStoppedTimes()).isOne();
    }

    @Test
    void shouldNotStopFileDiscoveryIfAlreadyStopped() {
        fileDiscoveryService.stopFileDiscovery();
        assertThat(sourceFileDiscoveryService.getStoppedTimes()).isZero();
    }

    private static class FakeSourceFileDiscoveryService implements SourceFileDiscoveryService {

        private final CountDownLatch finishLatch = new CountDownLatch(1);
        private final AtomicReference<Consumer<SourceFileDetails>> sourceFileDetailsConsumerRef =
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

        public void simulateFileDiscovery(SourceFileDetails sourceFileDetails) {
            sourceFileDetailsConsumerRef.get().accept(sourceFileDetails);
        }

        public void complete() {
            finishLatch.countDown();
        }

        @Override
        public String getSource() {
            return "someSource";
        }

        @Override
        public void startFileDiscovery(Consumer<SourceFileDetails> fileConsumer) {
            if (exception != null) {
                throw exception;
            }
            this.sourceFileDetailsConsumerRef.set(fileConsumer);
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