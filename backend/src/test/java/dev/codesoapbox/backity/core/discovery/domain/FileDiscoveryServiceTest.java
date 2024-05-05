package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.SourceFileDetails;
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
    private GameFileDetailsRepository fileRepository;

    @Mock
    private FileDiscoveryMessageService messageService;

    @BeforeEach
    void setUp() {
        sourceFileDiscoveryService = new FakeSourceFileDiscoveryService();
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, messageService);
    }

    @AfterEach
    void tearDown() {
        // Manually finish file discovery to prevent thread starvation
        sourceFileDiscoveryService.finishLatch.countDown();
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
    void startFileDiscoveryShouldSaveDiscoveredFilesAndSendMessages() {
        var gameTitle = "someGameTitle";
        var discoveredGameFile = new SourceFileDetails(
                new FileSourceId("someSource"), gameTitle, "someTitle", "someVersion", "someUrl",
                "someOriginalFileName", "100 KB");
        var game = new Game(GameId.newInstance(), gameTitle);
        GameFileDetails gameFileDetails = discoveredGameFile.associateWith(game);

        when(gameRepository.findByTitle(gameTitle))
                .thenReturn(Optional.of(game));

        when(fileRepository.existsByUrlAndVersion(discoveredGameFile.url(), discoveredGameFile.version()))
                .thenReturn(false);

        List<FileDiscoveryProgress> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(messageService).sendProgressUpdateMessage(any());
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, messageService);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredGameFile);

        var gameFileDetailsArgumentCaptor = ArgumentCaptor.forClass(GameFileDetails.class);
        verify(fileRepository).save(gameFileDetailsArgumentCaptor.capture());
        gameFileDetails.setId(gameFileDetailsArgumentCaptor.getValue().getId());
        verify(messageService).sendFileDiscoveredMessage(gameFileDetails);
        assertThat(progressList.size()).isOne();
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        SourceFileDetails gameFileVersionBackup = new SourceFileDetails(
                new FileSourceId("someSource"), "someGameTitle", "someTitle",
                "someVersion", "someUrl", "someOriginalFileName", "100 KB");

        when(fileRepository.existsByUrlAndVersion(gameFileVersionBackup.url(), gameFileVersionBackup.version()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersionBackup);

        verify(fileRepository, never()).save(any());
        verify(messageService, never()).sendFileDiscoveredMessage(any());
    }

    @Test
    void startFileDiscoveryShouldSetSourceServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.startFileDiscovery();

        sourceFileDiscoveryService.complete();

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().getFirst().isInProgress());
        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
    }

    @Test
    void startFileDiscoveryShouldNotTriggerWhenSourceDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.startFileDiscovery();
        sourceFileDiscoveryService.complete();
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().getFirst().isInProgress());

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertThat(sourceFileDiscoveryService.getTimesTriggered().get()).isOne();
    }

    @Test
    void shouldStopFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.stopFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().getFirst().isInProgress());
        assertThat(sourceFileDiscoveryService.getStoppedTimes()).isOne();
    }

    @Test
    void shouldNotStopFileDiscoveryIfAlreadyStopped() {
        fileDiscoveryService.stopFileDiscovery();
        assertThat(sourceFileDiscoveryService.getStoppedTimes()).isZero();
    }

    private static class FakeSourceFileDiscoveryService implements SourceFileDiscoveryService {

        private final CountDownLatch finishLatch = new CountDownLatch(1);
        private final AtomicReference<Consumer<SourceFileDetails>> gameFileVersionConsumer = new AtomicReference<>();

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
            gameFileVersionConsumer.get().accept(sourceFileDetails);
        }

        public void complete() {
            finishLatch.countDown();
        }

        @Override
        public String getSource() {
            return "someSource";
        }

        @Override
        public void startFileDiscovery(Consumer<SourceFileDetails> gameFileVersionConsumer) {
            if (exception != null) {
                throw exception;
            }
            this.gameFileVersionConsumer.set(gameFileVersionConsumer);
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