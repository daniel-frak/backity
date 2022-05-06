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
class FileDownloadQueueSchedulerTest {

    @InjectMocks
    private FileDownloadQueueScheduler fileDownloadQueueScheduler;

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

        fileDownloadQueueScheduler.processQueue();

        verify(fileDownloadQueue).markInProgress(enqueuedFileDownload);
        verify(fileDownloader).downloadGameFile(enqueuedFileDownload);
        verify(fileDownloadQueue).acknowledgeSuccess(enqueuedFileDownload);
        assertNull(fileDownloadQueueScheduler.enqueuedFileDownloadReference.get());
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

        fileDownloadQueueScheduler.processQueue();

        verify(fileDownloadQueue).acknowledgeFailed(enqueuedFileDownload, "someFailedReason");
        assertNull(fileDownloadQueueScheduler.enqueuedFileDownloadReference.get());
        verifyNoMoreInteractions(fileDownloadQueue, fileDownloader);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder().build();

        when(fileDownloadQueue.getOldestWaiting())
                .thenReturn(Optional.of(enqueuedFileDownload));
        when(fileDownloader.isReadyFor(enqueuedFileDownload))
                .thenReturn(false);

        fileDownloadQueueScheduler.processQueue();

        verifyNoMoreInteractions(fileDownloadQueue, fileDownloader);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        fileDownloadQueueScheduler.enqueuedFileDownloadReference.set(new EnqueuedFileDownload());

        fileDownloadQueueScheduler.processQueue();

        verifyNoInteractions(fileDownloadQueue, fileDownloader);
    }
}