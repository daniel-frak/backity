package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
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
    public GetDiscoveredFileCopiesUseCase getDiscoveredFileListUseCase(
            FileCopyRepository fileCopyRepository) {
        return new GetDiscoveredFileCopiesUseCase(fileCopyRepository);
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
            StorageSolution storageSolution, FileCopyRepository fileCopyRepository) {
        return new DeleteFileCopyUseCase(storageSolution, fileCopyRepository);
    }

    @Bean
    public DownloadFileCopyUseCase downloadFileUseCase(
            FileCopyRepository fileCopyRepository, StorageSolution storageSolution) {
        return new DownloadFileCopyUseCase(fileCopyRepository, storageSolution);
    }
}
