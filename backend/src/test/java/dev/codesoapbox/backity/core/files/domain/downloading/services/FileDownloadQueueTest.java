package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDownloadQueueTest {

    @InjectMocks
    private FileDownloadQueue fileDownloadQueue;

    @Mock
    private GameFileVersionRepository downloadRepository;

    @Mock
    private FileDownloadMessageService messageService;

    @Test
    void shouldEnqueue() {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .id(1L)
                .version("someVersion")
                .url("someUrl")
                .source("someSource")
                .name("someName")
                .gameTitle("someGameTitle")
                .size("someSize")
                .build();

        GameFileVersion expectedGameFileVersion = GameFileVersion.builder()
                .id(1L)
                .version("someVersion")
                .url("someUrl")
                .source("someSource")
                .url("someUrl")
                .name("someName")
                .gameTitle("someGameTitle")
                .version("someVersion")
                .size("someSize")
                .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                .build();

        fileDownloadQueue.enqueue(gameFileVersion);

        verify(downloadRepository).save(expectedGameFileVersion);
    }

    @Test
    void shouldGetOldestWaiting() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();

        when(downloadRepository.findOldestWaitingForDownload())
                .thenReturn(Optional.of(enqueuedFileDownload));

        var result = fileDownloadQueue.getOldestWaiting();

        assertTrue(result.isPresent());
        assertEquals(enqueuedFileDownload, result.get());
    }

    @Test
    void shouldAcknowledgeSuccess() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();

        fileDownloadQueue.acknowledgeSuccess(enqueuedFileDownload);

        assertEquals(FileStatus.DOWNLOADED, enqueuedFileDownload.getStatus());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendDownloadFinished(enqueuedFileDownload);
    }

    @Test
    void shouldAcknowledgeFailed() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();

        fileDownloadQueue.acknowledgeFailed(enqueuedFileDownload, "someFailedReason");

        assertEquals(FileStatus.DOWNLOAD_FAILED, enqueuedFileDownload.getStatus());
        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendDownloadFinished(enqueuedFileDownload);
    }

    @Test
    void shouldMarkInProgress() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();

        fileDownloadQueue.markInProgress(enqueuedFileDownload);

        assertEquals(FileStatus.DOWNLOAD_IN_PROGRESS, enqueuedFileDownload.getStatus());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendDownloadStarted(enqueuedFileDownload);
    }
}