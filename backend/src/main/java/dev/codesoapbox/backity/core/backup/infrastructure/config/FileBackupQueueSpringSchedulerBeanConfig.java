package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.schedule.FileBackupQueueSpringScheduler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringSchedulerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringSchedulerBeanConfiguration
public class FileBackupQueueSpringSchedulerBeanConfig {

    @Bean
    FileBackupQueueSpringScheduler fileBackupQueueSpringScheduler(
            BackUpOldestFileCopyUseCase backupOldestFileCopyUseCase) {
        return new FileBackupQueueSpringScheduler(backupOldestFileCopyUseCase);
    }
}
