package dev.codesoapbox.backity.core.backup.infrastructure.config.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFailedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupFinishedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers.FileBackupStartedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupProgressUpdatedWsEventMapper;
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
    FileBackupProgressChangedEventWebSocketHandler fileBackupStatusProgressedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupProgressUpdatedWsEventMapper backupProgressUpdatedMapper =
                Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);
        return new FileBackupProgressChangedEventWebSocketHandler(webSocketEventPublisher, backupProgressUpdatedMapper);
    }

    @Bean
    FileBackupFailedEventWebSocketHandler fileBackupFailedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupStatusChangedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);
        return new FileBackupFailedEventWebSocketHandler(webSocketEventPublisher, backupStatusChangedMapper);
    }
}
