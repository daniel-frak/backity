package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFailedEventWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFinishedEventWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupStartedEventWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileDownloadProgressChangedEventWebSocketListener;
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
    FileBackupStartedEventWebSocketListener fileBackupStartedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupStartedEventWebSocketListener(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventWebSocketListener fileBackupFinishedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupFinishedEventWebSocketListener(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileDownloadProgressChangedEventWebSocketListener fileDownloadProgressChangedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileDownloadProgressUpdatedWsEventMapper downloadProgressUpdatedMapper =
                Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

        return new FileDownloadProgressChangedEventWebSocketListener(
                webSocketEventPublisher, downloadProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventWebSocketListener fileBackupFailedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventWebSocketListener(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
