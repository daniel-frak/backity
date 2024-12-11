package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.application.BackUpOldestGameFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class FileBackupQueueSpringScheduler {

    private final BackUpOldestGameFileUseCase backUpOldestGameFileUseCase;

    @Scheduled(fixedRateString = "${file-download-queue-scheduler.rate-ms}")
    public void processQueue() {
        backUpOldestGameFileUseCase.backUpOldestGameFile();
    }
}
