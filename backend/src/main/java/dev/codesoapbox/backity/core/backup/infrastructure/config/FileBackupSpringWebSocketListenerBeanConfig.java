package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFailedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupFinishedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileBackupStartedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners.FileCopyReplicationProgressChangedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyReplicationProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringWebSocketListenerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@SpringWebSocketListenerBeanConfiguration
public class FileBackupSpringWebSocketListenerBeanConfig {

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
    FileCopyReplicationProgressChangedEventSpringWebSocketListener
    fileCopyReplicationProgressChangedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher) {
        FileCopyReplicationProgressUpdatedWsEventMapper fileCopyReplicationProgressUpdatedWsEventMapper =
                Mappers.getMapper(FileCopyReplicationProgressUpdatedWsEventMapper.class);

        return new FileCopyReplicationProgressChangedEventSpringWebSocketListener(
                springWebSocketEventPublisher, fileCopyReplicationProgressUpdatedWsEventMapper);
    }

    @Bean
    FileBackupFailedEventSpringWebSocketListener fileBackupFailedEventWebSocketListener(
            SpringWebSocketEventPublisher springWebSocketEventPublisher,
            FileCopyStatusChangedWsEventMapper fileCopyStatusChangedMapper) {
        return new FileBackupFailedEventSpringWebSocketListener(
                springWebSocketEventPublisher, fileCopyStatusChangedMapper);
    }
}
