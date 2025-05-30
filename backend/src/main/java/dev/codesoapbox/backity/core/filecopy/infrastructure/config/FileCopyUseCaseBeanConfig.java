package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyUseCaseBeanConfig {

    @Bean
    public FileCopyFactory fileCopyFactory() {
        return new FileCopyFactory(FileCopyId::newInstance);
    }

    @Bean
    public EnqueueFileCopyUseCase enqueueFileUseCase(FileCopyRepository fileCopyRepositoryRepository,
                                                     FileCopyFactory fileCopyFactory) {
        return new EnqueueFileCopyUseCase(fileCopyRepositoryRepository, fileCopyFactory);
    }

    @Bean
    public GetEnqueuedFileCopiesUseCase getEnqueuedFileListUseCase(
            FileCopyRepository fileCopyRepository) {
        return new GetEnqueuedFileCopiesUseCase(fileCopyRepository);
    }

    @Bean
    public GetProcessedFileCopiesUseCase getProcessedFileListUseCase(
            FileCopyRepository fileCopyRepository) {
        return new GetProcessedFileCopiesUseCase(fileCopyRepository);
    }

    @Bean
    public DeleteFileCopyUseCase deleteFileCopyUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DeleteFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Bean
    public DownloadFileCopyUseCase downloadFileUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DownloadFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }
}
