package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueuedFileDownloadProcessorTest {

    @InjectMocks
    private EnqueuedFileDownloadProcessor enqueuedFileDownloadProcessor;

    @Mock
    private GameFileVersionRepository gameFileVersionRepository;

    @Mock
    private FileDownloader fileDownloader;

    @Mock
    private FileDownloadMessageService messageService;

    @Test
    void shouldProcessEnqueuedFileDownloadIfNotCurrentlyDownloading() {
        var gameFileVersion = GameFileVersion.builder().build();

        when(gameFileVersionRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersion));
        when(fileDownloader.isReadyFor(gameFileVersion))
                .thenReturn(true);

        enqueuedFileDownloadProcessor.processQueue();

        verify(messageService).sendDownloadStarted(gameFileVersion);
        verify(fileDownloader).downloadGameFile(gameFileVersion);
        verify(messageService).sendDownloadFinished(gameFileVersion);
        assertNull(enqueuedFileDownloadProcessor.enqueuedFileDownloadReference.get());
    }

    @Test
    void shouldFailGracefully() {
        var gameFileVersion = GameFileVersion.builder().build();

        when(gameFileVersionRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersion));
        when(fileDownloader.isReadyFor(gameFileVersion))
                .thenReturn(true);
        doThrow(new RuntimeException("someFailedReason"))
                .when(fileDownloader).downloadGameFile(gameFileVersion);

        enqueuedFileDownloadProcessor.processQueue();

        verify(messageService).sendDownloadStarted(gameFileVersion);
        verify(messageService).sendDownloadFinished(gameFileVersion);
        assertNull(enqueuedFileDownloadProcessor.enqueuedFileDownloadReference.get());
        verifyNoMoreInteractions(messageService, fileDownloader);
    }

    @Test
    void shouldDoNothingIfSourceDownloaderNotReady() {
        var gameFileVersion = GameFileVersion.builder().build();

        when(gameFileVersionRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(gameFileVersion));
        when(fileDownloader.isReadyFor(gameFileVersion))
                .thenReturn(false);

        enqueuedFileDownloadProcessor.processQueue();

        verifyNoMoreInteractions(messageService, fileDownloader);
    }

    @Test
    void shouldDoNothingIfCurrentlyDownloading() {
        enqueuedFileDownloadProcessor.enqueuedFileDownloadReference.set(new GameFileVersion());

        enqueuedFileDownloadProcessor.processQueue();

        verifyNoInteractions(messageService, fileDownloader);
    }
}