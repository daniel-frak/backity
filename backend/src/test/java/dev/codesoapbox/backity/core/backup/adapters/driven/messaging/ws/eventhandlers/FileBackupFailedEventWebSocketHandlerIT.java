package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvents;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@WebSocketEventHandlerTest
class FileBackupFailedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileBackupFailedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileBackupFailedEvent event = TestFileBackupEvents.failed();

        eventHandler.handle(event);

        String receivedMessage = messageChannel.receiveMessage(
                FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination());
        String expectedJson = """
                {
                  "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                  "newStatus": "FAILED",
                  "failedReason": "some failed reason"
                }
                """;
        assertReceivedMessageIs(receivedMessage, expectedJson);
    }

    private void assertReceivedMessageIs(String receivedMessage, String expectedJson) throws JsonProcessingException {
        assertThat(receivedMessage).isNotNull();
        assertThat(objectMapper.readTree(receivedMessage))
                .isEqualTo(objectMapper.readTree(expectedJson));
    }
}