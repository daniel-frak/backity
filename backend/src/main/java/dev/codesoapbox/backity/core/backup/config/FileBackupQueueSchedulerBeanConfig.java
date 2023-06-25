package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupQueueSchedulerBeanConfig {

    @Bean
    public FileBackupQueueSpringScheduler fileBackupQueueSpringScheduler(
            EnqueuedFileBackupProcessor enqueuedFileBackupProcessor) {
        return new FileBackupQueueSpringScheduler(enqueuedFileBackupProcessor);
    }
}
