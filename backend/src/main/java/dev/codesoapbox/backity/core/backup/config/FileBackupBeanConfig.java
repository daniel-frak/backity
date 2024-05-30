package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupEventWebSocketPublisher;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import dev.codesoapbox.backity.core.backup.domain.FileBackupEventPublisher;
import dev.codesoapbox.backity.core.backup.domain.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.SourceFileBackupService;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
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
    FileBackupService fileDownloader(FilePathProvider filePathProvider,
                                     FileDetailsRepository fileDetailsSpringRepository,
                                     List<SourceFileBackupService> fileDownloaders,
                                     FileManager fileManager) {
        return new FileBackupService(filePathProvider, fileDetailsSpringRepository, fileManager, fileDownloaders);
    }

    @Bean
    FileBackupEventPublisher fileBackupEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        FileBackupStartedWsEventMapper backupStartedMapper = Mappers.getMapper(FileBackupStartedWsEventMapper.class);
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);
        return new FileBackupEventWebSocketPublisher(simpMessagingTemplate, backupStartedMapper,
                backupStatusChangedMapper);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(FileDetailsRepository fileDetailsRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupEventPublisher fileBackupEventPublisher) {
        return new EnqueuedFileBackupProcessor(fileDetailsRepository, fileBackupService, fileBackupEventPublisher);
    }
}
