package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvents;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@WebSocketEventHandlerTest
class FileBackupStartedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileBackupStartedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileBackupStartedEvent event = TestFileBackupEvents.started();

        eventHandler.handle(event);

        String receivedMessage = messageChannel.receiveMessage(
                FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination());
        String expectedJson = """
                {
                  "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                  "originalGameTitle": "Original Game Title",
                  "fileTitle": "fileTitle",
                  "version": "1.0.0",
                  "originalFileName": "originalFileName",
                  "size": "5 KB",
                  "filePath": "file/path"
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