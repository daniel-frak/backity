package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadQueueProcessorTest {

    @InjectMocks
    private FileDownloadQueueProcessor fileDownloadQueueProcessor;

    @Mock
    private FileDownloadQueue fileDownloadQueue;

    @Mock
    private FileDownloader fileDownloader;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder().build();

        when(fileDownloadQueue.getOldestWaiting())
                .thenReturn(Optional.of(enqueuedFileDownload));
        when(fileDownloader.isReadyFor(enqueuedFileDownload))
                .thenReturn(true);

        fileDownloadQueueProcessor.processQueue();

        verify(fileDownloadQueue).markInProgress(enqueuedFileDownload);
        verify(fileDownloader).downloadGameFile(enqueuedFileDownload);
        verify(fileDownloadQueue).acknowledgeSuccess(enqueuedFileDownload);
        assertNull(fileDownloadQueueProcessor.enqueuedFileDownloadReference.get());
    }

    @Test
    void shouldFailGracefully() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder().build();

        when(fileDownloadQueue.getOldestWaiting())
                .thenReturn(Optional.of(enqueuedFileDownload));
        when(fileDownloader.isReadyFor(enqueuedFileDownload))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileDownloader).downloadGameFile(enqueuedFileDownload);

        fileDownloadQueueProcessor.processQueue();

        verify(fileDownloadQueue).markInProgress(enqueuedFileDownload);
        verify(fileDownloadQueue).acknowledgeFailed(enqueuedFileDownload, "someFailedReason");
        assertNull(fileDownloadQueueProcessor.enqueuedFileDownloadReference.get());
        verifyNoMoreInteractions(fileDownloadQueue, fileDownloader);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder().build();

        when(fileDownloadQueue.getOldestWaiting())
                .thenReturn(Optional.of(enqueuedFileDownload));
        when(fileDownloader.isReadyFor(enqueuedFileDownload))
                .thenReturn(false);

        fileDownloadQueueProcessor.processQueue();

        verifyNoMoreInteractions(fileDownloadQueue, fileDownloader);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        fileDownloadQueueProcessor.enqueuedFileDownloadReference.set(new EnqueuedFileDownload());

        fileDownloadQueueProcessor.processQueue();

        verifyNoInteractions(fileDownloadQueue, fileDownloader);
    }
}