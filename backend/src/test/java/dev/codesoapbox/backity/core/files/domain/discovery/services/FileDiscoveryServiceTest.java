package dev.codesoapbox.backity.core.files.domain.discovery.services;

import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDiscoveryServiceTest {

    private FileDiscoveryService fileDiscoveryService;

    private FakeSourceFileDiscoveryService sourceFileDiscoveryService;

    @Mock
    private GameFileVersionRepository repository;

    @Mock
    private FileDiscoveryMessageService messageService;

    @BeforeEach
    void setUp() {
        sourceFileDiscoveryService = new FakeSourceFileDiscoveryService();
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                repository, messageService);
    }

    @Test
    void shouldStartFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();

        assertTrue(fileDiscoveryService.getStatuses().get(0).isInProgress());
    }

    @Test
    void startFileDiscoveryShouldSaveDiscoveredFilesAndSendMessages() {
        GameFileVersion gameFileVersion = new GameFileVersion();
        gameFileVersion.setId(1L);
        gameFileVersion.setUrl("someUrl");
        gameFileVersion.setVersion("someVersion");

        when(repository.existsByUrlAndVersion(gameFileVersion.getUrl(), gameFileVersion.getVersion()))
                .thenReturn(false);

        List<FileDiscoveryProgress> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(0));
            return null;
        }).when(messageService).sendProgress(any());
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                repository, messageService);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersion);

        verify(repository).save(gameFileVersion);
        verify(messageService).sendDiscoveredFile(gameFileVersion);
        assertEquals(1, progressList.size());
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        var gameFileVersion = new GameFileVersion();
        gameFileVersion.setId(1L);
        gameFileVersion.setUrl("someUrl");
        gameFileVersion.setVersion("someVersion");

        when(repository.existsByUrlAndVersion(gameFileVersion.getUrl(), gameFileVersion.getVersion()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(gameFileVersion);

        verify(repository, never()).save(any());
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
        private final AtomicReference<Consumer<GameFileVersion>> gameFileVersionConsumer = new AtomicReference<>();

        @Getter
        private int stoppedTimes = 0;

        @Getter
        private final AtomicInteger timesTriggered = new AtomicInteger();

        public boolean hasBeenTriggered() {
            return timesTriggered.get() > 0;
        }

        public void simulateFileDiscovery(GameFileVersion gameFileVersion) {
            gameFileVersionConsumer.get().accept(gameFileVersion);
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
        public void startFileDiscovery(Consumer<GameFileVersion> gameFileVersionConsumer) {
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