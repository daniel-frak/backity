package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class FileDiscoveryServiceTest {

    private FileDiscoveryService fileDiscoveryService;

    private FakeSourceFileDiscoveryService sourceFileDiscoveryService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameFileVersionBackupRepository fileRepository;

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
    void CompletedFileDiscoveryHandlerShouldLogExceptionAndChangeStatusOnFailure(CapturedOutput capturedOutput) {
        FileDiscoveryService.CompletedFileDiscoveryHandler handler =
                fileDiscoveryService.getCompletedFileDiscoveryHandler();

        handler.handle(sourceFileDiscoveryService).accept(null, new RuntimeException("test exception"));

        assertThat(capturedOutput.getAll())
                .contains("test exception");

        assertThat(fileDiscoveryService.getStatuses().size()).isOne();
        assertFalse(fileDiscoveryService.getStatuses().get(0).isInProgress());
    }

    @Test
    void startFileDiscoveryShouldSaveDiscoveredFilesAndSendMessages() {
        GameFileVersionBackup gameFileVersionBackup = new GameFileVersionBackup();
        gameFileVersionBackup.setId(1L);
        gameFileVersionBackup.setGameTitle("someGameTitle");
        gameFileVersionBackup.setUrl("someUrl");
        gameFileVersionBackup.setVersion("someVersion");

        when(fileRepository.existsByUrlAndVersion(gameFileVersionBackup.getUrl(), gameFileVersionBackup.getVersion()))
                .thenReturn(false);

        List<FileDiscoveryProgress> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(messageService).sendProgress(any());
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                gameRepository, fileRepository, messageService);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersionBackup);

        verify(fileRepository).save(gameFileVersionBackup);
        verify(messageService).sendDiscoveredFile(gameFileVersionBackup);
        assertEquals(1, progressList.size());
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        var gameFileVersionBackup = new GameFileVersionBackup();
        gameFileVersionBackup.setId(1L);
        gameFileVersionBackup.setGameTitle("someGameTitle");
        gameFileVersionBackup.setUrl("someUrl");
        gameFileVersionBackup.setVersion("someVersion");

        when(fileRepository.existsByUrlAndVersion(gameFileVersionBackup.getUrl(), gameFileVersionBackup.getVersion()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersionBackup);

        verify(fileRepository, never()).save(any());
        verify(messageService, never()).sendDiscoveredFile(any());
    }

    @Test
    void startFileDiscoveryShouldSetSourceServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.startFileDiscovery();

        sourceFileDiscoveryService.complete();

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
    }

    @Test
    void startFileDiscoveryShouldNotTriggerWhenSourceDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.startFileDiscovery();
        sourceFileDiscoveryService.complete();

        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
        assertEquals(1, sourceFileDiscoveryService.getTimesTriggered().get());
    }

    @Test
    void shouldStopFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
        fileDiscoveryService.stopFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
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
        private final AtomicReference<Consumer<GameFileVersionBackup>> gameFileVersionConsumer = new AtomicReference<>();

        @Getter
        private int stoppedTimes = 0;

        @Getter
        private final AtomicInteger timesTriggered = new AtomicInteger();

        @Setter
        private RuntimeException exception;

        public boolean hasBeenTriggered() {
            return timesTriggered.get() > 0;
        }

        public void simulateFileDiscovery(GameFileVersionBackup gameFileVersionBackup) {
            gameFileVersionConsumer.get().accept(gameFileVersionBackup);
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
        public void startFileDiscovery(Consumer<GameFileVersionBackup> gameFileVersionConsumer) {
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