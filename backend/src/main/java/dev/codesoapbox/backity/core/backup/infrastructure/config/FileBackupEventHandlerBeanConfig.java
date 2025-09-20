package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.FileBackupFinishedRepositoryHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.FileDownloadProgressChangedRepositoryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupEventHandlerBeanConfig {

    @Bean
    FileDownloadProgressChangedRepositoryHandler fileDownloadProgressChangedRepositoryHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileDownloadProgressChangedRepositoryHandler(replicationProgressRepository);
    }

    @Bean
    FileBackupFinishedRepositoryHandler fileBackupFinishedRepositoryHandler(
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileBackupFinishedRepositoryHandler(replicationProgressRepository);
    }
}
