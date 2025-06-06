package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventHandlerTest
class FileDownloadProgressChangedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileDownloadProgressChangedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileDownloadProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        eventHandler.handle(event);

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