package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupEventWebSocketPublisher;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import dev.codesoapbox.backity.core.backup.domain.FileBackupEventPublisher;
import dev.codesoapbox.backity.core.backup.domain.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    FileBackupEventPublisher fileBackupEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        FileBackupStartedWsEventMapper backupStartedMapper = Mappers.getMapper(FileBackupStartedWsEventMapper.class);
        FileBackupProgressUpdatedWsEventMapper progressUpdatedMapper =
                Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);
        return new FileBackupEventWebSocketPublisher(simpMessagingTemplate, backupStartedMapper, progressUpdatedMapper,
                backupStatusChangedMapper);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(GameFileRepository gameFileRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupEventPublisher fileBackupEventPublisher) {
        return new EnqueuedFileBackupProcessor(gameFileRepository, fileBackupService, fileBackupEventPublisher);
    }
}
