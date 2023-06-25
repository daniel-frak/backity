package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class FileBackupQueueSpringScheduler {

    private final EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Scheduled(fixedRateString = "${file-download-queue-scheduler.rate-ms}")
    public synchronized void processQueue() {
        enqueuedFileBackupProcessor.processQueue();
    }
}
