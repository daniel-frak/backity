package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFailedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFinishedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileDownloadProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupStartedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupWebSocketBeanConfig {

    @Bean
    FileBackupStartedEventWebSocketHandler fileBackupStartedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupStartedWsEventMapper backupStartedMapper =
                Mappers.getMapper(FileBackupStartedWsEventMapper.class);
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

        return new FileBackupStartedEventWebSocketHandler(
                webSocketEventPublisher, backupStartedMapper, backupStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventWebSocketHandler fileBackupFinishedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

        return new FileBackupFinishedEventWebSocketHandler(webSocketEventPublisher, backupStatusChangedMapper);
    }

    @Bean
    FileDownloadProgressChangedEventWebSocketHandler fileBackupStatusProgressedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileDownloadProgressUpdatedWsEventMapper downloadProgressUpdatedMapper =
                Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

        return new FileDownloadProgressChangedEventWebSocketHandler(
                webSocketEventPublisher, downloadProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventWebSocketHandler fileBackupFailedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

        return new FileBackupFailedEventWebSocketHandler(webSocketEventPublisher, backupStatusChangedMapper);
    }
}
