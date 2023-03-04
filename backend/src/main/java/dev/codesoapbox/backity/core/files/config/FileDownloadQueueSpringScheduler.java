package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.domain.downloading.services.EnqueuedFileDownloadProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDownloadQueueSpringScheduler {

    private final EnqueuedFileDownloadProcessor enqueuedFileDownloadProcessor;

    @Scheduled(fixedRateString = "${file-download-queue-scheduler.rate-ms}")
    public synchronized void processQueue() {
        enqueuedFileDownloadProcessor.processQueue();
    }
}
