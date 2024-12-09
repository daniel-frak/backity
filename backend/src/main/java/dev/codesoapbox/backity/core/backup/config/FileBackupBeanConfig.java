package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import dev.codesoapbox.backity.core.backup.domain.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileBackupBeanConfig {

    @Bean
    FileBackupService fileBackupService(FilePathProvider filePathProvider,
                                        GameFileRepository gameFileSpringRepository,
                                        List<GameProviderFileBackupService> fileBackupServices,
                                        FileManager fileManager) {
        return new FileBackupService(filePathProvider, gameFileSpringRepository, fileManager, fileBackupServices);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(GameFileRepository gameFileRepository,
                                                           FileBackupService fileBackupService) {
        return new EnqueuedFileBackupProcessor(gameFileRepository, fileBackupService);
    }

    @Bean
    BackupProgressFactory backupProgressFactory(DomainEventPublisher domainEventPublisher) {
        return new BackupProgressFactory(domainEventPublisher);
    }
}
