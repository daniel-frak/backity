package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners.BackupRecoveryCompletedEventListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners.FileBackupFinishedEventListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners.FileDownloadProgressChangedEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupEventHandlerBeanConfig {

    @Bean
    FileDownloadProgressChangedEventListener fileDownloadProgressChangedRepositoryHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileDownloadProgressChangedEventListener(replicationProgressRepository);
    }

    @Bean
    FileBackupFinishedEventListener fileBackupFinishedRepositoryHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileBackupFinishedEventListener(replicationProgressRepository);
    }

    @Bean
    BackupRecoveryCompletedEventListener backupRecoveryCompletedEventHandler(
            FileCopyReplicationProcess fileCopyReplicationProcess) {
        return new BackupRecoveryCompletedEventListener(fileCopyReplicationProcess);
    }
}
