package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;

@WebSocketEventHandlerTest
class FileBackupStartedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileBackupStartedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameFileRepository gameFileRepository;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileBackupStartedEvent event = TestFileBackupEvent.started();
        GameFile gameFile = TestGameFile.gog();
        when(gameFileRepository.getById(event.fileCopyNaturalId().gameFileId()))
                .thenReturn(gameFile);

        eventHandler.handle(event);

        var expectedJson = """
                {
                  "fileCopyId": "6df888e8-90b9-4df5-a237-0cba422c0310",
                  "fileCopyNaturalId": {
                    "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "backupTargetId": "d46dde81-e519-4300-9a54-6f9e7d637926"
                  },
                  "originalGameTitle": "Game 1",
                  "fileTitle": "Game 1 (Installer)",
                  "version": "1.0.0",
                  "originalFileName": "game_1_installer.exe",
                  "size": "5 KB",
                  "filePath": "file/path"
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination(), expectedJson);
    }
}