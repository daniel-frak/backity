package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.DomainEventHandlerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@DomainEventHandlerBeanConfiguration
public class FileBackupEventHandlerBeanConfig {

    @Bean
    FileCopyReplicationProgressChangedEventHandler fileCopyReplicationProgressChangedEventHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileCopyReplicationProgressChangedEventHandler(replicationProgressRepository);
    }

    @Bean
    FileBackupFinishedEventHandler fileBackupFinishedEventHandler(
            FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository) {
        return new FileBackupFinishedEventHandler(fileCopyReplicationProgressRepository);
    }

    @Bean
    BackupRecoveryCompletedEventHandler backupRecoveryCompletedEventHandler(
            FileCopyReplicationProcess fileCopyReplicationProcess) {
        return new BackupRecoveryCompletedEventHandler(fileCopyReplicationProcess);
    }
}
