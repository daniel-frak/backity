package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventForwarderTest;
import dev.codesoapbox.backity.testing.messaging.websockets.TestMessageChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventForwarderTest
class FileBackupFailedEventWebSocketForwarderIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileBackupFailedEventWebSocketForwarder forwarder;

    @Test
    void shouldPublishWebSocketEvent() {
        FileBackupFailedEvent event = TestFileBackupEvent.failed();

        forwarder.forward(event);

        var expectedJson = """
                {
                  "fileCopyId": "6df888e8-90b9-4df5-a237-0cba422c0310",
                  "fileCopyNaturalId": {
                    "sourceFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "backupTargetId": "d46dde81-e519-4300-9a54-6f9e7d637926"
                  },
                  "newStatus": "FAILED",
                  "failedReason": "some failed reason"
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), expectedJson);
    }
}