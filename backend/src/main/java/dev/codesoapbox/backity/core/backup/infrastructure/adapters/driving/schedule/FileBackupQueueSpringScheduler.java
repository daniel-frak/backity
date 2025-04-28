package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestGameFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class FileBackupQueueSpringScheduler {

    private final BackUpOldestGameFileUseCase backUpOldestGameFileUseCase;

    @Scheduled(fixedRateString = "${backity.file-download-queue-scheduler.rate-ms}")
    public void processQueue() {
        backUpOldestGameFileUseCase.backUpOldestGameFile();
    }
}
