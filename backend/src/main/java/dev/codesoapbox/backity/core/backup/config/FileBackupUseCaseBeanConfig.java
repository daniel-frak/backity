package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.application.BackUpOldestGameFileUseCase;
import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileBackupUseCaseBeanConfig {

    @Bean
    FileBackupService fileBackupService(FilePathProvider filePathProvider,
                                        GameFileRepository gameFileSpringRepository,
                                        List<GameProviderFileBackupService> fileBackupServices,
                                        FileManager fileManager,
                                        BackupProgressFactory backupProgressFactory) {
        return new FileBackupService(filePathProvider, gameFileSpringRepository, fileManager, fileBackupServices,
                backupProgressFactory);
    }

    @Bean
    BackupProgressFactory backupProgressFactory(DomainEventPublisher domainEventPublisher) {
        return new BackupProgressFactory(domainEventPublisher);
    }

    @Bean
    BackUpOldestGameFileUseCase backUpOldestGameFileUseCase(GameFileRepository gameFileRepository,
                                                            FileBackupService fileBackupService) {
        return new BackUpOldestGameFileUseCase(gameFileRepository, fileBackupService);
    }
}
