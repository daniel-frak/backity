package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.messaging.FileBackupSpringMessageService;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessageMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsSpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.JpaGameFileDetailsMapper;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
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
                                     GameFileDetailsRepository gameFileDetailsSpringRepository,
                                     List<SourceFileBackupService> fileDownloaders,
                                     FileManager fileManager) {
        return new FileBackupService(filePathProvider, gameFileDetailsSpringRepository, fileManager, fileDownloaders);
    }

    @Bean
    JpaGameFileDetailsMapper jpaGameFileDetailsMapper() {
        return Mappers.getMapper(JpaGameFileDetailsMapper.class);
    }

    @Bean
    GameFileDetailsRepository gameFileVersionRepository(GameFileDetailsSpringRepository springRepository,
                                                        JpaGameFileDetailsMapper mapper) {
        return new GameFileDetailsJpaRepository(springRepository, mapper);
    }

    @Bean
    FileBackupMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileBackupSpringMessageService(simpMessagingTemplate, GameFileDetailsMessageMapper.INSTANCE);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(GameFileDetailsRepository gameFileDetailsRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupMessageService fileBackupMessageService) {
        return new EnqueuedFileBackupProcessor(gameFileDetailsRepository, fileBackupService, fileBackupMessageService);
    }
}
