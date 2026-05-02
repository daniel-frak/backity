package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ClearProgressOnFileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.application.usecases.ProcessFileCopyQueueUseCase;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.DomainEventHandlerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@DomainEventHandlerBeanConfiguration
public class FileBackupEventHandlerBeanConfig {

    @Bean
    SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler
    saveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler(
                replicationProgressRepository);
    }

    @Bean
    ClearProgressOnFileBackupFinishedEventHandler clearProgressOnFileBackupFinishedEventHandler(
            FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository) {
        return new ClearProgressOnFileBackupFinishedEventHandler(fileCopyReplicationProgressRepository);
    }

    @Bean
    MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler
    markBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler(
            FileCopyReplicationProcess fileCopyReplicationProcess) {
        return new MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler(fileCopyReplicationProcess);
    }

    @Bean
    ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler processFileCopyQueueOnFileCopyEnqueuedEventHandler(
            ProcessFileCopyQueueUseCase processFileCopyQueueUseCase) {
        return new ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler(processFileCopyQueueUseCase);
    }
}
