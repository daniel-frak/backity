package dev.codesoapbox.backity.core.files.downloading.config;

import dev.codesoapbox.backity.core.files.downloading.domain.services.FileDownloadQueueProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDownloadQueueSpringScheduler {

    private final FileDownloadQueueProcessor fileDownloadQueueProcessor;

    @Scheduled(fixedRateString = "${file-download-queue-scheduler.rate-ms}")
    public synchronized void processQueue() {
        fileDownloadQueueProcessor.processQueue();
    }
}
