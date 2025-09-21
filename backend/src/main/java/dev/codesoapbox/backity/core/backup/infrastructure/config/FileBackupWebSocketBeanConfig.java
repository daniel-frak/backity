package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFailedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFinishedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupStartedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileDownloadProgressChangedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
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
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupStartedEventSpringWebSocketListener(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventSpringWebSocketListener fileBackupFinishedEventWebSocketListener(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupFinishedEventSpringWebSocketListener(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileDownloadProgressChangedEventSpringWebSocketListener fileDownloadProgressChangedEventWebSocketListener(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileDownloadProgressUpdatedWsEventMapper downloadProgressUpdatedMapper =
                Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

        return new FileDownloadProgressChangedEventSpringWebSocketListener(
                webSocketEventPublisher, downloadProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventSpringWebSocketListener fileBackupFailedEventWebSocketListener(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventSpringWebSocketListener(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
