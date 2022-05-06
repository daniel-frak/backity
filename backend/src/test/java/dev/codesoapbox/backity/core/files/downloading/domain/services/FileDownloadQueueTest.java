package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.model.messages.FileDownloadMessageTopics;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDownloadQueueTest {

    @InjectMocks
    private FileDownloadQueue fileDownloadQueue;

    @Mock
    private DiscoveredFileRepository discoveredFileRepository;

    @Mock
    private EnqueuedFileDownloadRepository downloadRepository;

    @Mock
    private MessageService messageService;

    @Test
    void shouldEnqueue() {
        DiscoveredFile discoveredFile = DiscoveredFile.builder()
                .id(new DiscoveredFileId("someUrl", "someVersion"))
                .source("someSource")
                .name("someName")
                .gameTitle("someGameTitle")
                .size("someSize")
                .build();

        fileDownloadQueue.enqueue(discoveredFile);

        assertTrue(discoveredFile.isEnqueued());
        verify(discoveredFileRepository).save(discoveredFile);

        ArgumentCaptor<EnqueuedFileDownload> enqueuedFileDownloadCaptor = ArgumentCaptor.forClass(EnqueuedFileDownload.class);
        verify(downloadRepository).save(enqueuedFileDownloadCaptor.capture());

        EnqueuedFileDownload expectedEnqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(enqueuedFileDownloadCaptor.getValue().getId())
                .source("someSource")
                .url("someUrl")
                .name("someName")
                .gameTitle("someGameTitle")
                .version("someVersion")
                .size("someSize")
                .status(DownloadStatus.WAITING)
                .build();

        assertEquals(expectedEnqueuedFileDownload, enqueuedFileDownloadCaptor.getValue());
    }

    @Test
    void shouldGetOldestWaiting() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        when(downloadRepository.findOldestWaiting())
                .thenReturn(Optional.of(enqueuedFileDownload));

        var result = fileDownloadQueue.getOldestWaiting();

        assertTrue(result.isPresent());
        assertEquals(enqueuedFileDownload, result.get());
    }

    @Test
    void shouldAcknowledgeSuccess() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        fileDownloadQueue.acknowledgeSuccess(enqueuedFileDownload);

        assertEquals(DownloadStatus.DOWNLOADED, enqueuedFileDownload.getStatus());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(),
                enqueuedFileDownload);
    }

    @Test
    void shouldAcknowledgeFailed() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        fileDownloadQueue.acknowledgeFailed(enqueuedFileDownload, "someFailedReason");

        assertEquals(DownloadStatus.FAILED, enqueuedFileDownload.getStatus());
        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(),
                enqueuedFileDownload);
    }

    @Test
    void shouldFindAllWaiting() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(downloadRepository.findAllWaiting(pageable))
                .thenReturn(new PageImpl<>(singletonList(enqueuedFileDownload)));

        var result = fileDownloadQueue.findAllQueued(pageable);

        assertEquals(1, result.getSize());
        assertEquals(enqueuedFileDownload, result.getContent().get(0));
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        when(downloadRepository.findCurrentlyDownloading())
                .thenReturn(Optional.of(enqueuedFileDownload));

        var result = fileDownloadQueue.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertEquals(enqueuedFileDownload, result.get());
    }

    @Test
    void shouldMarkInProgress() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        fileDownloadQueue.markInProgress(enqueuedFileDownload);

        assertEquals(DownloadStatus.IN_PROGRESS, enqueuedFileDownload.getStatus());

        verify(downloadRepository).save(enqueuedFileDownload);
        verify(messageService).sendMessage(FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(), enqueuedFileDownload);
    }

    @Test
    void shouldFindAllProcessed() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(downloadRepository.findAllProcessed(pageable))
                .thenReturn(new PageImpl<>(singletonList(enqueuedFileDownload)));

        var result = fileDownloadQueue.findAllProcessed(pageable);

        assertEquals(1, result.getSize());
        assertEquals(enqueuedFileDownload, result.getContent().get(0));
    }
}