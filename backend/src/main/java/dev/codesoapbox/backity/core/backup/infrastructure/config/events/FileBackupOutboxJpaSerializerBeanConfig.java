package dev.codesoapbox.backity.core.backup.infrastructure.config.events;


import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox.*;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.OutboxJpaSerializerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@OutboxJpaSerializerBeanConfiguration
public class FileBackupOutboxJpaSerializerBeanConfig {

    @Bean
    BackupRecoveryCompletedEventOutboxJpaSerializer backupRecoveryCompletedEventOutboxJpaSerializer() {
        return new BackupRecoveryCompletedEventOutboxJpaSerializer();
    }

    @Bean
    FileBackupFailedEventOutboxJpaSerializer fileBackupFailedEventOutboxJpaSerializer() {
        return new FileBackupFailedEventOutboxJpaSerializer();
    }

    @Bean
    FileBackupFinishedEventOutboxJpaSerializer fileBackupFinishedEventOutboxJpaSerializer() {
        return new FileBackupFinishedEventOutboxJpaSerializer();
    }

    @Bean
    FileBackupStartedEventOutboxJpaSerializer fileBackupStartedEventOutboxJpaSerializer() {
        return new FileBackupStartedEventOutboxJpaSerializer();
    }

    @Bean
    FileCopyReplicationProgressChangedEventOutboxJpaSerializer
    fileCopyReplicationProgressChangedEventOutboxJpaSerializer() {
        return new FileCopyReplicationProgressChangedEventOutboxJpaSerializer();
    }
}
