package dev.codesoapbox.gogbackupservice.files.downloading.application.services;

import dev.codesoapbox.gogbackupservice.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.gogbackupservice.integrations.gog.application.services.auth.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDownloadQueueScheduler {

    private final GogAuthService authService;
    private final FileDownloadQueue fileDownloadQueue;
    private final FileDownloader fileDownloader;
    private final AtomicReference<EnqueuedFileDownload> enqueuedFileDownloadReference = new AtomicReference<>();

    @Scheduled(fixedRate = 5000)
    public synchronized void processQueue() {
        if (!authService.isAuthenticated() || enqueuedFileDownloadReference.get() != null) {
            return;
        }

        fileDownloadQueue.getOldestUnprocessed()
                .ifPresent(this::processEnqueuedFileDownload);
    }

    private void processEnqueuedFileDownload(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownloadReference.set(enqueuedFileDownload);

        log.info("Downloading enqueued file {}", enqueuedFileDownload.getUrl());

        try {
            fileDownloader.downloadGameFile(enqueuedFileDownload);
            fileDownloadQueue.acknowledgeSuccess(enqueuedFileDownload);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued file (id: {})",
                    enqueuedFileDownload.getId(), e);
            fileDownloadQueue.acknowledgeFailed(enqueuedFileDownload);
        } finally {
            enqueuedFileDownloadReference.set(null);
        }
    }
}
