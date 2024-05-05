package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileBackupProcessorTest {

    @InjectMocks
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Mock
    private FileDetailsRepository fileDetailsRepository;

    @Mock
    private FileBackupService fileBackupService;

    @Mock
    private FileBackupMessageService messageService;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        FileDetails fileDetails = discoveredFileDetails().build();
        AtomicBoolean fileDetailsWasKeptAsReferenceDuringProcessing = new AtomicBoolean();
        when(fileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(fileDetails));
        when(fileBackupService.isReadyFor(fileDetails))
                .thenReturn(true);
        doAnswer(inv -> {
            fileDetailsWasKeptAsReferenceDuringProcessing.set(
                    enqueuedFileBackupProcessor.enqueuedFileBackupReference.get() == fileDetails);
            return null;
        }).when(fileBackupService).backUpFile(fileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(fileDetails);
        verify(fileBackupService).backUpFile(fileDetails);
        verify(messageService).sendBackupFinished(fileDetails);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        assertThat(fileDetailsWasKeptAsReferenceDuringProcessing).isTrue();
    }

    @Test
    void shouldFailGracefully() {
        FileDetails fileDetails = discoveredFileDetails().build();

        when(fileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(fileDetails));
        when(fileBackupService.isReadyFor(fileDetails))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileBackupService).backUpFile(fileDetails);

        enqueuedFileBackupProcessor.processQueue();

        verify(messageService).sendBackupStarted(fileDetails);
        verify(messageService).sendBackupFinished(fileDetails);
        assertThat(enqueuedFileBackupProcessor.enqueuedFileBackupReference.get()).isNull();
        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        FileDetails fileDetails = discoveredFileDetails().build();

        when(fileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(fileDetails));
        when(fileBackupService.isReadyFor(fileDetails))
                .thenReturn(false);

        enqueuedFileBackupProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileBackupService);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        FileDetails fileDetails = discoveredFileDetails().build();
        lenient().when(fileDetailsRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(fileDetails));

        enqueuedFileBackupProcessor.enqueuedFileBackupReference.set(fileDetails);
        enqueuedFileBackupProcessor.processQueue();

        verifyNoInteractions(fileDetailsRepository, fileBackupService, messageService);
    }
}