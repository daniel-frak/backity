package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.messaging.FileBackupSpringMessageService;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.JpaGameFileVersionMapper;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.backup.services.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileBackupBeanConfig {

    @Bean
    FileBackupService fileDownloader(FilePathProvider filePathProvider,
                                     GameFileVersionRepository gameFileVersionRepository,
                                     List<SourceFileBackupService> fileDownloaders,
                                     FileManager fileManager) {
        return new FileBackupService(filePathProvider, gameFileVersionRepository, fileManager, fileDownloaders);
    }

    @Bean
    JpaGameFileVersionMapper jpaGameFileVersionMapper() {
        return Mappers.getMapper(JpaGameFileVersionMapper.class);
    }

    @Bean
    GameFileVersionRepository gameFileVersionRepository(GameFileVersionSpringRepository springRepository,
                                                        JpaGameFileVersionMapper mapper) {
        return new GameFileVersionJpaRepository(springRepository, mapper);
    }

    @Bean
    FileBackupMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileBackupSpringMessageService(simpMessagingTemplate);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(GameFileVersionRepository gameFileVersionRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupMessageService fileBackupMessageService) {
        return new EnqueuedFileBackupProcessor(gameFileVersionRepository, fileBackupService, fileBackupMessageService);
    }
}
