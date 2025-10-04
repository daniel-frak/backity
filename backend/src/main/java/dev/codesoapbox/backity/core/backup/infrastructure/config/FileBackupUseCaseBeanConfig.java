package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileBackupContextFactory;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class FileBackupUseCaseBeanConfig {

    @Bean
    BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase(FileCopyReplicationProcess fileCopyReplicationProcess,
                                                            FileCopyRepository fileCopyRepository,
                                                            FileBackupContextFactory fileBackupContextFactory,
                                                            FileBackupService fileBackupService) {
        return new BackUpOldestFileCopyUseCase(fileCopyReplicationProcess, fileCopyRepository, fileBackupContextFactory,
                fileBackupService);
    }

    @Bean
    RecoverInterruptedFileBackupUseCase recoverInterruptedFileBackupUseCase(
            FileCopyRepository fileCopyRepository, BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository, DomainEventPublisher domainEventPublisher) {
        return new RecoverInterruptedFileBackupUseCase(fileCopyRepository, backupTargetRepository,
                storageSolutionRepository, domainEventPublisher);
    }
}
