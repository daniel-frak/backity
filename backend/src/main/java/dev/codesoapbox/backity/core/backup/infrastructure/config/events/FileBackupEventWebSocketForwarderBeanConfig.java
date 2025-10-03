package dev.codesoapbox.backity.core.backup.infrastructure.config.events;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders.FileBackupFailedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders.FileBackupFinishedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders.FileBackupStartedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders.FileCopyReplicationProgressChangedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyReplicationProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@WebSocketEventForwarderBeanConfiguration
public class FileBackupEventWebSocketForwarderBeanConfig {

    @Bean
    FileCopyStatusChangedWsEventMapper fileCopyStatusChangedWsEventMapper() {
        return Mappers.getMapper(FileCopyStatusChangedWsEventMapper.class);
    }

    @Bean
    FileBackupStartedEventWebSocketForwarder fileBackupStartedEventWebSocketForwarder(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupStartedEventWebSocketForwarder(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileBackupFinishedEventWebSocketForwarder fileBackupFinishedEventWebSocketForwarder(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {

        return new FileBackupFinishedEventWebSocketForwarder(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }

    @Bean
    FileCopyReplicationProgressChangedEventWebSocketForwarder
    fileCopyReplicationProgressChangedEventWebSocketForwarder(
            WebSocketEventPublisher webSocketEventPublisher) {
        FileCopyReplicationProgressUpdatedWsEventMapper fileCopyReplicationProgressUpdatedWsEventMapper =
                Mappers.getMapper(FileCopyReplicationProgressUpdatedWsEventMapper.class);

        return new FileCopyReplicationProgressChangedEventWebSocketForwarder(
                webSocketEventPublisher, fileCopyReplicationProgressUpdatedWsEventMapper);
    }

    @Bean
    FileBackupFailedEventWebSocketForwarder fileBackupFailedEventWebSocketForwarder(
            WebSocketEventPublisher webSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventWebSocketForwarder(
                webSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
