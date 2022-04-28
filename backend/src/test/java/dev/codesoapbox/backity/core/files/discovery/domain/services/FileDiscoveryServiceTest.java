package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileSpringRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryMessageTopics;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDiscoveryServiceTest {

    private FileDiscoveryService fileDiscoveryService;

    private FakeSourceFileDiscoveryService sourceFileDiscoveryService;

    @Mock
    private DiscoveredFileSpringRepository repository;

    @Mock
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        sourceFileDiscoveryService = new FakeSourceFileDiscoveryService();
        fileDiscoveryService = new FileDiscoveryService(singletonList(sourceFileDiscoveryService),
                repository, messageService);
    }

    @Test
    void discoverNewFilesShouldStartFileDiscovery() {
        fileDiscoveryService.discoverNewFiles();

        assertTrue(fileDiscoveryService.getStatuses().get(0).isInProgress());
    }

    @Test
    void discoverNewFilesShouldSaveDiscoveredFilesAndSendMessages() {
        DiscoveredFile discoveredFile = new DiscoveredFile();
        discoveredFile.setId(new DiscoveredFileId("someUrl", "someVersion"));

        when(repository.existsById(discoveredFile.getId()))
                .thenReturn(false);

        fileDiscoveryService.discoverNewFiles();

        await().atMost(5, TimeUnit.SECONDS)
                .until(sourceFileDiscoveryService::hasBeenTriggered);
        sourceFileDiscoveryService.simulateFileDiscovery(discoveredFile);

        verify(repository).save(discoveredFile);
        verify(messageService).sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(), discoveredFile);
    }

    @Test
    void discoverNewFilesShouldSetSourceServiceAsNotInProgressWhenDone() {
        fileDiscoveryService.discoverNewFiles();

        sourceFileDiscoveryService.complete();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
    }

    @Test
    void discoverNewFilesShouldNotTriggerWhenSourceDiscoveryServiceAlreadyInProgress() {
        fileDiscoveryService.discoverNewFiles();
        fileDiscoveryService.discoverNewFiles();
        sourceFileDiscoveryService.complete();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !fileDiscoveryService.getStatuses().get(0).isInProgress());
        assertEquals(1, fileDiscoveryService.getStatuses().size());
        assertEquals(1, sourceFileDiscoveryService.getTimesTriggered().get());
    }

    private static class FakeSourceFileDiscoveryService implements SourceFileDiscoveryService {

        private final AtomicBoolean shouldFinish = new AtomicBoolean(false);
        private final AtomicReference<Consumer<DiscoveredFile>> discoveredFileConsumer = new AtomicReference<>();

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

        @Override
        public void discoverNewFiles(Consumer<DiscoveredFile> discoveredFileConsumer) {
            this.discoveredFileConsumer.set(discoveredFileConsumer);
            timesTriggered.incrementAndGet();

            while (!shouldFinish.get()) {
                try {
                    // Do nothing
                    TimeUnit.MILLISECONDS.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {

        }

        @Override
        public ProgressInfo getProgress() {
            return null;
        }
    }
}