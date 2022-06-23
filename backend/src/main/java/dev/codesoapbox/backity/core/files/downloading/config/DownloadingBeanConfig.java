package dev.codesoapbox.backity.core.files.downloading.config;

import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence.EnqueuedFileDownloadJpaRepository;
import dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence.EnqueuedFileDownloadSpringRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.services.*;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DownloadingBeanConfig {

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
    EnqueuedFileDownloader enqueuedFileDownloader(FileManager fileManager) {
        return new EnqueuedFileDownloader(fileManager);
    }

    @Bean
    EnqueuedFileDownloadRepository enqueuedFileDownloadRepository(
            EnqueuedFileDownloadSpringRepository springRepository) {
        return new EnqueuedFileDownloadJpaRepository(springRepository);
    }

    @Bean
    FileDownloadQueue fileDownloadQueue(DiscoveredFileRepository discoveredFileRepository,
                                        EnqueuedFileDownloadRepository downloadRepository,
                                        MessageService messageService) {
        return new FileDownloadQueue(discoveredFileRepository, downloadRepository, messageService);
    }

    @Bean
    FileDownloadQueueScheduler fileDownloadQueueScheduler(FileDownloadQueue fileDownloadQueue,
                                                          FileDownloader fileDownloader) {
        return new FileDownloadQueueScheduler(fileDownloadQueue, fileDownloader);
    }
}
