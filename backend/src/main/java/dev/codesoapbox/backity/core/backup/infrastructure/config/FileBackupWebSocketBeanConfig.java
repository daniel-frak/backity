package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFailedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFinishedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupStartedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileDownloadProgressChangedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupWebSocketBeanConfig {

    @Bean
    FileCopyStatusChangedWsEventMapper fileCopyStatusChangedWsEventMapper() {
        return Mappers.getMapper(FileCopyStatusChangedWsEventMapper.class);
    }

    @Bean
    FileBackupStartedEventSpringWebSocketListener fileBackupStartedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupStartedEventSpringWebSocketListener(
                springWebSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventSpringWebSocketListener fileBackupFinishedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupFinishedEventSpringWebSocketListener(
                springWebSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileDownloadProgressChangedEventSpringWebSocketListener fileDownloadProgressChangedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher) {
        FileDownloadProgressUpdatedWsEventMapper downloadProgressUpdatedMapper =
                Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

        return new FileDownloadProgressChangedEventSpringWebSocketListener(
                springWebSocketEventPublisher, downloadProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventSpringWebSocketListener fileBackupFailedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventSpringWebSocketListener(
                springWebSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
