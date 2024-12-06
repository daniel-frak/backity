package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers.FileBackupFailedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers.FileBackupFinishedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers.FileBackupProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers.FileBackupStartedEventWebSocketHandler;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupWebSocketBeanConfig {

    @Bean
    FileBackupStartedEventWebSocketHandler fileBackupStartedEventWebSocketHandler(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileBackupStartedWsEventMapper backupStatusChangedMapper =
                Mappers.getMapper(FileBackupStartedWsEventMapper.class);

        return new FileBackupStartedEventWebSocketHandler(webSocketEventPublisher, backupStatusChangedMapper);
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
