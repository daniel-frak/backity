package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestGameFileUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.schedule.FileBackupQueueSpringScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupQueueSchedulerBeanConfig {

    @Bean
    public FileBackupQueueSpringScheduler fileBackupQueueSpringScheduler(
            BackUpOldestGameFileUseCase backupOldestGameFileUseCase) {
        return new FileBackupQueueSpringScheduler(backupOldestGameFileUseCase);
    }
}
