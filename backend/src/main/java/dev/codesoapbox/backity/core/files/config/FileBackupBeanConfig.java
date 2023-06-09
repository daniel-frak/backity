package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.messaging.FileBackupSpringMessageService;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaBackupRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import dev.codesoapbox.backity.core.files.domain.backup.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileBackupBeanConfig {

    @Bean
    FileBackupService fileDownloader(FilePathProvider filePathProvider,
                                     GameFileVersionBackupRepository gameFileVersionBackupRepository,
                                     List<SourceFileBackupService> fileDownloaders,
                                     FileManager fileManager) {
        return new FileBackupService(filePathProvider, gameFileVersionBackupRepository, fileManager, fileDownloaders);
    }

    @Bean
    GameFileVersionBackupRepository gameFileVersionRepository(GameFileVersionSpringRepository springRepository) {
        return new GameFileVersionJpaBackupRepository(springRepository);
    }

    @Bean
    FileBackupMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileBackupSpringMessageService(simpMessagingTemplate);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(GameFileVersionBackupRepository gameFileVersionBackupRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupMessageService fileBackupMessageService) {
        return new EnqueuedFileBackupProcessor(gameFileVersionBackupRepository, fileBackupService, fileBackupMessageService);
    }
}
