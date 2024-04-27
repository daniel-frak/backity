package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.SourceFileDetails;
import lombok.Getter;
import lombok.Setter;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldStartFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertTrue(fileDiscoveryService.getStatuses().get(0).isInProgress());
    }

    @Test
    void completedFileDiscoveryHandlerShouldChangeStatusOnFailure() {
        FileDiscoveryService.CompletedFileDiscoveryHandler handler =
                fileDiscoveryService.getCompletedFileDiscoveryHandler();

        handler.handle(sourceFileDiscoveryService).accept(null, new RuntimeException("test exception"));

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertFalse(fileDiscoveryService.getStatuses().get(0).isInProgress());
    }

    @Test
    void startFileDiscoveryShouldSaveDiscoveredFilesAndSendMessages() {
        var gameTitle = "someGameTitle";
        var discoveredGameFile = new SourceFileDetails(
                "someSource", gameTitle, "someTitle", "someVersion", "someUrl",
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

        await().atMost(5, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredGameFile);

        var gameFileDetailsArgumentCaptor = ArgumentCaptor.forClass(GameFileDetails.class);
        verify(fileRepository).save(gameFileDetailsArgumentCaptor.capture());
        gameFileDetails.setId(gameFileDetailsArgumentCaptor.getValue().getId());
        verify(messageService).sendFileDiscoveredMessage(gameFileDetails);
        assertEquals(1, progressList.size());
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        SourceFileDetails gameFileVersionBackup = new SourceFileDetails(
                "someSource", "someGameTitle", "someTitle", "someVersion", "someUrl",
                "someOriginalFileName", "100 KB");

        when(fileRepository.existsByUrlAndVersion(gameFileVersionBackup.url(), gameFileVersionBackup.version()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(5, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersionBackup);

        verify(fileRepository, never()).save(any());
        verify(messageService, never()).sendFileDiscoveredMessage(any());
    }

    @Test
    void startFileDiscoveryShouldSetSourceServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.startFileDiscovery();

        sourceFileDiscoveryService.complete();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
    }

    @Test
    void startFileDiscoveryShouldNotTriggerWhenSourceDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.startFileDiscovery();
        sourceFileDiscoveryService.complete();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
        assertEquals(1, sourceFileDiscoveryService.getTimesTriggered().get());
    }

    @Test
    void shouldStopFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.stopFileDiscovery();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, sourceFileDiscoveryService.getStoppedTimes());
    }

    @Test
    void shouldNotStopFileDiscoveryIfAlreadyStopped() {
        fileDiscoveryService.stopFileDiscovery();
        assertEquals(0, sourceFileDiscoveryService.getStoppedTimes());
    }

    private static class FakeSourceFileDiscoveryService implements SourceFileDiscoveryService {

        private final AtomicBoolean shouldFinish = new AtomicBoolean(false);
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
            shouldFinish.set(true);
        }

        @Override
        public String getSource() {
            return "someSource";
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void startFileDiscovery(Consumer<SourceFileDetails> gameFileVersionConsumer) {
            if (exception != null) {
                throw exception;
            }
            this.gameFileVersionConsumer.set(gameFileVersionConsumer);
            timesTriggered.incrementAndGet();

            while (!shouldFinish.get()) {
                // Do nothing
            }
        }

        @Override
        public void stopFileDiscovery() {
            stoppedTimes++;
            shouldFinish.set(true);
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