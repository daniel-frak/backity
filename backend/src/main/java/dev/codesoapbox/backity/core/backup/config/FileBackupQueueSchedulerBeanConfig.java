package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.application.BackUpOldestGameFileUseCase;
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
