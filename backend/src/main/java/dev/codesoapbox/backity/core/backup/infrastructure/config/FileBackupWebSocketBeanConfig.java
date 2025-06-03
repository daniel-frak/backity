package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFailedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFinishedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupStartedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileDownloadProgressChangedEventWebSocketHandler;
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
    FileBackupStartedEventWebSocketHandler fileBackupStartedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupStartedEventWebSocketHandler(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventWebSocketHandler fileBackupFinishedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupFinishedEventWebSocketHandler(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileDownloadProgressChangedEventWebSocketHandler fileDownloadProgressChangedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileDownloadProgressUpdatedWsEventMapper downloadProgressUpdatedMapper =
                Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

        return new FileDownloadProgressChangedEventWebSocketHandler(
                webSocketEventPublisher, downloadProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventWebSocketHandler fileBackupFailedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventWebSocketHandler(webSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
