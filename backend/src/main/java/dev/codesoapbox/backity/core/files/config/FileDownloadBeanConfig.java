package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.FileDownloadSpringMessageService;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.downloading.services.*;
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
    FileDownloader fileDownloader(FilePathProvider filePathProvider,
                                  GameFileVersionRepository gameFileVersionRepository,
                                  List<SourceFileDownloader> fileDownloaders,
                                  FileManager fileManager) {
        return new FileDownloader(filePathProvider, gameFileVersionRepository, fileManager, fileDownloaders);
    }

    @Bean
    GameFileVersionRepository gameFileVersionRepository(GameFileVersionSpringRepository springRepository) {
        return new GameFileVersionJpaRepository(springRepository);
    }

    @Bean
    FileDownloadMessageService fileDownloadMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileDownloadSpringMessageService(simpMessagingTemplate);
    }

    @Bean
    EnqueuedFileDownloadProcessor fileDownloadQueueScheduler(GameFileVersionRepository gameFileVersionRepository,
                                                             FileDownloader fileDownloader,
                                                             FileDownloadMessageService fileDownloadMessageService) {
        return new EnqueuedFileDownloadProcessor(gameFileVersionRepository, fileDownloader, fileDownloadMessageService);
    }
}
