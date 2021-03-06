package dev.codesoapbox.backity.core.files.downloading.config;

import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.messaging.FileDownloadSpringMessageService;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence.EnqueuedFileDownloadJpaRepository;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence.EnqueuedFileDownloadSpringRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.services.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileDownloadBeanConfig {

    @Bean
    FileManager fileManager() {
        return new RealFileManager();
    }

    @Bean
    FilePathProvider filePathProvider(@Value("${default-path-template}") String defaultPathTemplate,
                                      FileManager fileManager) {
        return new FilePathProvider(defaultPathTemplate, fileManager);
    }

    @Bean
    FileDownloader fileDownloader(FilePathProvider filePathProvider, List<SourceFileDownloader> fileDownloaders,
                                  FileManager fileManager) {
        return new FileDownloader(filePathProvider, fileManager, fileDownloaders);
    }

    @Bean
    EnqueuedFileDownloadRepository enqueuedFileDownloadRepository(
            EnqueuedFileDownloadSpringRepository springRepository) {
        return new EnqueuedFileDownloadJpaRepository(springRepository);
    }

    @Bean
    FileDownloadMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileDownloadSpringMessageService(simpMessagingTemplate);
    }

    @Bean
    FileDownloadQueue fileDownloadQueue(DiscoveredFileRepository discoveredFileRepository,
                                        EnqueuedFileDownloadRepository downloadRepository,
                                        FileDownloadMessageService messageService) {
        return new FileDownloadQueue(discoveredFileRepository, downloadRepository, messageService);
    }

    @Bean
    FileDownloadQueueProcessor fileDownloadQueueScheduler(FileDownloadQueue fileDownloadQueue,
                                                          FileDownloader fileDownloader) {
        return new FileDownloadQueueProcessor(fileDownloadQueue, fileDownloader);
    }
}
