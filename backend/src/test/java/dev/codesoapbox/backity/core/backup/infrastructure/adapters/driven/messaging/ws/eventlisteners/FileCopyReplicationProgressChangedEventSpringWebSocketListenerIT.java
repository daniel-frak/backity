package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringWebSocketEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@SpringWebSocketEventListenerTest
class FileCopyReplicationProgressChangedEventSpringWebSocketListenerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileCopyReplicationProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        applicationEventPublisher.publishEvent(event);

        var expectedJson = """
                {
                  "fileCopyId": "6df888e8-90b9-4df5-a237-0cba422c0310",
                  "fileCopyNaturalId": {
                    "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "backupTargetId": "d46dde81-e519-4300-9a54-6f9e7d637926"
                  },
                  "percentage": 50,
                  "timeLeftSeconds": 999
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                FileBackupWebSocketTopics.BACKUP_PROGRESS_CHANGED.wsDestination(), expectedJson);
    }
}