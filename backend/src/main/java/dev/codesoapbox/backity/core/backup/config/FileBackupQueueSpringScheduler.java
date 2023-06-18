package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileBackupQueueSpringScheduler {

    private final EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Scheduled(fixedRateString = "${file-download-queue-scheduler.rate-ms}")
    public synchronized void processQueue() {
        enqueuedFileBackupProcessor.processQueue();
    }
}