package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestGameFileUseCase;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileBackupUseCaseBeanConfig {

    @Bean
    FileBackupService fileBackupService(FilePathProvider filePathProvider,
                                        GameFileRepository gameFileSpringRepository,
                                        List<GameProviderFileBackupService> fileBackupServices,
                                        StorageSolution storageSolution,
                                        DownloadProgressFactory downloadProgressFactory) {
        return new FileBackupService(filePathProvider, gameFileSpringRepository, storageSolution, fileBackupServices,
                downloadProgressFactory);
    }

    @Bean
    DownloadProgressFactory downloadProgressFactory(DomainEventPublisher domainEventPublisher) {
        return new DownloadProgressFactory(domainEventPublisher);
    }

    @Bean
    BackUpOldestGameFileUseCase backUpOldestGameFileUseCase(GameFileRepository gameFileRepository,
                                                            FileBackupService fileBackupService) {
        return new BackUpOldestGameFileUseCase(gameFileRepository, fileBackupService);
    }
}
