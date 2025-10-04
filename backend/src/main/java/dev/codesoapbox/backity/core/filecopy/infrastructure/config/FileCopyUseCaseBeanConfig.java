package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.backup.application.StorageSolutionWriteService;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class FileCopyUseCaseBeanConfig {

    @Bean
    EnqueueFileCopyUseCase enqueueFileUseCase(FileCopyRepository fileCopyRepository,
                                              FileCopyFactory fileCopyFactory) {
        return new EnqueueFileCopyUseCase(fileCopyRepository, fileCopyFactory);
    }

    @Bean
    CancelFileCopyUseCase cancelFileCopyUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionWriteService storageSolutionWriteService) {
        return new CancelFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionWriteService);
    }

    @Bean
    GetFileCopyQueueUseCase getEnqueuedFileListUseCase(
            FileCopyRepository fileCopyRepository, FileCopyWithContextFactory fileCopyWithContextFactory) {
        return new GetFileCopyQueueUseCase(fileCopyRepository, fileCopyWithContextFactory);
    }

    @Bean
    DeleteFileCopyUseCase deleteFileCopyUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DeleteFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Bean
    DownloadFileCopyUseCase downloadFileCopyUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DownloadFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }
}
