package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileBackupProcessorTest {

    @InjectMocks
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Mock
    private FileBackupEventPublisher eventPublisher;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        GameFile gameFile = discoveredGameFile().build();
        AtomicBoolean gameFileWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
        doAnswer(inv -> {
            gameFileWasKeptAsReferenceDuringProcessing.set(
                    enqueuedFileBackupProcessor.enqueuedFileBackupReference.get() == gameFile);
            return null;
        }).when(fileBackupService).backUpFile(gameFile);

        enqueuedFileBackupProcessor.processQueue();

        verify(eventPublisher).publishBackupStartedEvent(gameFile);
        verify(fileBackupService).backUpFile(gameFile);
        verify(eventPublisher).publishBackupFinishedEvent(gameFile);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        assertThat(gameFileWasKeptAsReferenceDuringProcessing).isTrue();
    }

    @Test
    void shouldFailGracefully() {
        GameFile gameFile = discoveredGameFile().build();

        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(gameFile);

        enqueuedFileBackupProcessor.processQueue();

        verify(eventPublisher).publishBackupStartedEvent(gameFile);
        verify(eventPublisher).publishBackupFinishedEvent(gameFile);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        verifyNoMoreInteractions(eventPublisher, fileBackupService);
    }

    @Test
    void shouldDoNothingIfGameProviderIdDownloaderNotReady() {
        GameFile gameFile = discoveredGameFile().build();

        when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));
        when(fileBackupService.isReadyFor(gameFile))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(eventPublisher, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        GameFile gameFile = discoveredGameFile().build();
        lenient().when(gameFileRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFile));

        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(gameFile);
        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(gameFileRepository, fileBackupService, eventPublisher);
    }
}