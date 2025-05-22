package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileBackupUseCaseBeanConfig {

    @Bean
    FileBackupService fileBackupService(UniqueFilePathResolver uniqueFilePathResolver,
                                        FileCopyRepository fileCopyRepository,
                                        List<GameProviderFileBackupService> fileBackupServices,
                                        StorageSolution storageSolution,
                                        DownloadProgressFactory downloadProgressFactory) {
        return new FileBackupService(uniqueFilePathResolver, fileCopyRepository, storageSolution,
                fileBackupServices, downloadProgressFactory);
    }

    @Bean
    DownloadProgressFactory downloadProgressFactory(DomainEventPublisher domainEventPublisher) {
        return new DownloadProgressFactory(domainEventPublisher);
    }

    @Bean
    BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase(GameFileRepository gameFileRepository,
                                                            FileCopyRepository fileCopyRepository,
                                                            FileBackupService fileBackupService) {
        return new BackUpOldestFileCopyUseCase(fileCopyRepository, gameFileRepository, fileBackupService);
    }
}
