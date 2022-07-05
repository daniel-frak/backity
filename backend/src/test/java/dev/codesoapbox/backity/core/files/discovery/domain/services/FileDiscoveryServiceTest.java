package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryMessageTopics;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
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
    private DiscoveredFileRepository repository;

    @Mock
    private MessageService messageService;

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
        DiscoveredFile discoveredFile = new DiscoveredFile();
        discoveredFile.setId(new DiscoveredFileId("someUrl", "someVersion"));

        when(repository.existsById(discoveredFile.getId()))
                .thenReturn(false);

        List<FileDiscoveryProgress> progressList = new ArrayList<>();
        doAnswer(inv -> {
            progressList.add(inv.getArgument(1));
            return null;
        }).when(messageService).sendMessage(eq(FileDiscoveryMessageTopics.FILE_DISCOVERY_PROGRESS.toString()), any());
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                repository, messageService);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        verify(repository).save(discoveredFile);
        verify(messageService).sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(), discoveredFile);
        assertEquals(1, progressList.size());
    }

    @Test
    void startFileDiscoveryShouldNotSaveDiscoveredFileIfAlreadyExists() {
        DiscoveredFile discoveredFile = new DiscoveredFile();
        discoveredFile.setId(new DiscoveredFileId("someUrl", "someVersion"));

        when(repository.existsById(discoveredFile.getId()))
                .thenReturn(true);

        fileDiscoveryService.startFileDiscovery();

        await().atMost(2, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        verify(repository, never()).save(any());
        verify(messageService, never()).sendMessage(eq(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString()), any());
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
        private final AtomicReference<Consumer<DiscoveredFile>> discoveredFileConsumer = new AtomicReference<>();

        @Getter
        private int stoppedTimes = 0;

        @Getter
        private final AtomicInteger timesTriggered = new AtomicInteger();

        public boolean hasBeenTriggered() {
            return timesTriggered.get() > 0;
        }

        public void simulateFileDiscovery(DiscoveredFile discoveredFile) {
            discoveredFileConsumer.get().accept(discoveredFile);
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
        public void startFileDiscovery(Consumer<DiscoveredFile> discoveredFileConsumer) {
            this.discoveredFileConsumer.set(discoveredFileConsumer);
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