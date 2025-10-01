package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.*;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.DomainEventHandlerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@DomainEventHandlerBeanConfiguration
public class FileBackupEventHandlerBeanConfig {

    @Bean
    FileCopyReplicationProgressChangedEventHandler fileCopyReplicationProgressChangedEventHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository,
            FileCopyReplicationProgressChangedEventExternalForwarder eventForwarder) {
        return new FileCopyReplicationProgressChangedEventHandler(replicationProgressRepository, eventForwarder);
    }

    @Bean
    public FileBackupStartedEventHandler fileBackupStartedEventHandler(
            FileBackupStartedEventExternalForwarder eventForwarder) {
        return new FileBackupStartedEventHandler(eventForwarder);
    }

    @Bean
    public FileBackupFinishedEventHandler fileBackupFinishedEventHandler(
            FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository,
            FileBackupFinishedEventExternalForwarder eventForwarder) {
        return new FileBackupFinishedEventHandler(fileCopyReplicationProgressRepository, eventForwarder);
    }

    @Bean
    BackupRecoveryCompletedEventHandler backupRecoveryCompletedEventHandler(
            FileCopyReplicationProcess fileCopyReplicationProcess) {
        return new BackupRecoveryCompletedEventHandler(fileCopyReplicationProcess);
    }

    @Bean
    FileBackupFailedEventHandler fileBackupFailedEventHandler(FileBackupFailedEventExternalForwarder eventForwarder) {
        return new FileBackupFailedEventHandler(eventForwarder);
    }
}
