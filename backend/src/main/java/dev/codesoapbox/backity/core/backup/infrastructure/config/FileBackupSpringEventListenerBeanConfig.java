package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.BackupRecoveryCompletedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.FileBackupFinishedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.FileCopyReplicationProgressChangedEventSpringListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringEventListenerBeanConfiguration
public class FileBackupSpringEventListenerBeanConfig {

    @Bean
    FileCopyReplicationProgressChangedEventSpringListener fileCopyReplicationProgressChangedEventSpringListener(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileCopyReplicationProgressChangedEventSpringListener(replicationProgressRepository);
    }

    @Bean
    FileBackupFinishedEventSpringListener fileBackupFinishedEventSpringListener(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileBackupFinishedEventSpringListener(replicationProgressRepository);
    }

    @Bean
    BackupRecoveryCompletedEventSpringListener backupRecoveryCompletedEventSpringListener(
            FileCopyReplicationProcess fileCopyReplicationProcess) {
        return new BackupRecoveryCompletedEventSpringListener(fileCopyReplicationProcess);
    }
}
