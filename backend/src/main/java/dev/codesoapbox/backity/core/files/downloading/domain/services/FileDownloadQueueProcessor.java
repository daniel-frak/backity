package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class FileDownloadQueueProcessor {

    private final FileDownloadQueue fileDownloadQueue;
    private final FileDownloader fileDownloader;

    final AtomicReference<EnqueuedFileDownload> enqueuedFileDownloadReference = new AtomicReference<>();

    public synchronized void processQueue() {
        if (enqueuedFileDownloadReference.get() != null) {
            return;
        }

        fileDownloadQueue.getOldestWaiting()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(EnqueuedFileDownload enqueuedFileDownload) {
        if (!fileDownloader.isReadyFor(enqueuedFileDownload)) {
            return;
        }

        enqueuedFileDownloadReference.set(enqueuedFileDownload);

        log.info("Downloading enqueued file {}", enqueuedFileDownload.getUrl());

        try {
            fileDownloadQueue.markInProgress(enqueuedFileDownload);
            fileDownloader.downloadGameFile(enqueuedFileDownload);
            fileDownloadQueue.acknowledgeSuccess(enqueuedFileDownload);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    enqueuedFileDownload.getId(), e);
            fileDownloadQueue.acknowledgeFailed(enqueuedFileDownload, e.getMessage());
        } finally {
            enqueuedFileDownloadReference.set(null);
        }
    }
}
