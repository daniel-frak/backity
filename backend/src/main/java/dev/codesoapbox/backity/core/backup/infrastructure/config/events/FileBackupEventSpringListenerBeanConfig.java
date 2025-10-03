package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.*;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.*;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringEventListenerBeanConfiguration
public class FileBackupEventSpringListenerBeanConfig {

    @Bean
    FileCopyReplicationProgressChangedEventSpringListener fileCopyReplicationProgressChangedEventSpringListener(
            FileCopyReplicationProgressChangedEventHandler eventHandler) {
        return new FileCopyReplicationProgressChangedEventSpringListener(eventHandler);
    }

    @Bean
    FileBackupFinishedEventSpringListener fileBackupFinishedEventSpringListener(
            FileBackupFinishedEventHandler eventHandler) {
        return new FileBackupFinishedEventSpringListener(eventHandler);
    }

    @Bean
    BackupRecoveryCompletedEventSpringListener backupRecoveryCompletedEventSpringListener(
            BackupRecoveryCompletedEventHandler eventHandler) {
        return new BackupRecoveryCompletedEventSpringListener(eventHandler);
    }
}
