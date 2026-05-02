package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ClearProgressOnFileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.ClearProgressOnFileBackupFinishedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.ProcessFileCopyQueueOnFileCopyEnqueuedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.shared.messaging.spring.outbox.FileCopyEnqueuedOutboxEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@SpringEventListenerBeanConfiguration
public class FileBackupEventSpringListenerBeanConfig {

    @Bean
    SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListener
    saveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListener(
            SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler eventHandler) {
        return new SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListener(eventHandler);
    }

    @Bean
    ClearProgressOnFileBackupFinishedEventSpringListener clearProgressOnFileBackupFinishedEventSpringListener(
            ClearProgressOnFileBackupFinishedEventHandler eventHandler) {
        return new ClearProgressOnFileBackupFinishedEventSpringListener(eventHandler);
    }

    @Bean
    MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListener
    markBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListener(
            MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler eventHandler) {
        return new MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListener(eventHandler);
    }

    @Bean
    ProcessFileCopyQueueOnFileCopyEnqueuedEventSpringListener
    processFileCopyQueueOnFileCopyEnqueuedEventSpringListener(
            ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler eventHandler) {
        return new ProcessFileCopyQueueOnFileCopyEnqueuedEventSpringListener(eventHandler);
    }

    @Bean
    FileCopyEnqueuedOutboxEventMapper fileCopyEnqueuedOutboxEventMapper() {
        return Mappers.getMapper(FileCopyEnqueuedOutboxEventMapper.class);
    }
}
