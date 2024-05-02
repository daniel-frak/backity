package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileBackupProcessorTest {

    @InjectMocks
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Mock
    private FileBackupMessageService messageService;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        AtomicBoolean gameFileDetailsWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(true);
        doAnswer(inv -> {
            gameFileDetailsWasKeptAsReferenceDuringProcessing.set(
                    enqueuedFileBackupProcessor.enqueuedFileBackupReference.get() == gameFileDetails);
            return null;
        }).when(fileBackupService).backUpGameFile(gameFileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileDetails);
        verify(fileBackupService).backUpGameFile(gameFileDetails);
        verify(messageService).sendBackupFinished(gameFileDetails);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        assertThat(gameFileDetailsWasKeptAsReferenceDuringProcessing).isTrue();
    }

    @Test
    void shouldFailGracefully() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpGameFile(gameFileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileDetails);
        verify(messageService).sendBackupFinished(gameFileDetails);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        lenient().when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));

        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(gameFileDetails);
        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(gameFileDetailsRepository, fileBackupService, messageService);
    }
}