package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        GameFileDetails gameFileDetails = discovered().build();

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(true);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileDetails);
        verify(fileBackupService).backUpGameFile(gameFileDetails);
        verify(messageService).sendBackupFinished(gameFileDetails);
        assertNull(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get());
    }

    @Test
    void shouldFailGracefully() {
        GameFileDetails gameFileDetails = discovered().build();

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpGameFile(gameFileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(gameFileDetails);
        verify(messageService).sendBackupFinished(gameFileDetails);
        assertNull(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get());
        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        GameFileDetails gameFileDetails = discovered().build();

        when(gameFileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileDetails));
        when(fileBackupService.isReadyFor(gameFileDetails))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        GameFileDetails gameFileDetails = discovered().build();
        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(gameFileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(messageService, fileBackupService);
    }
}