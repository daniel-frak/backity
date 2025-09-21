package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class FileBackupQueueSpringScheduler {

    private final BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase;

    @Scheduled(fixedRateString = "${backity.file-backup-queue-scheduler.rate-ms}")
    public void processQueue() {
        backUpOldestFileCopyUseCase.backUpOldestFileCopy();
    }
}
