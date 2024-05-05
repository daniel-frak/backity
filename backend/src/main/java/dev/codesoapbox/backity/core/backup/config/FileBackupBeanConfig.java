package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupSpringMessageService;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedMessageMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import dev.codesoapbox.backity.core.backup.domain.FileBackupMessageService;
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
    FileBackupMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        FileBackupStartedMessageMapper backupStartedMapper = Mappers.getMapper(FileBackupStartedMessageMapper.class);
        FileBackupStatusChangedMessageMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedMessageMapper.class);
        return new FileBackupSpringMessageService(simpMessagingTemplate, backupStartedMapper,
                backupStatusChangedMapper);
    }

    @Bean
    EnqueuedFileBackupProcessor fileDownloadQueueScheduler(FileDetailsRepository fileDetailsRepository,
                                                           FileBackupService fileBackupService,
                                                           FileBackupMessageService fileBackupMessageService) {
        return new EnqueuedFileBackupProcessor(fileDetailsRepository, fileBackupService, fileBackupMessageService);
    }
}
