package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
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
    private FileCopyRepository fileCopyRepository;

    @Autowired
    private GameFileRepository gameFileRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileBackupStartedEvent event = TestFileBackupEvent.started();
        GameFile gameFile = TestGameFile.gog();
        when(fileCopyRepository.getById(event.fileCopyId()))
                .thenReturn(TestFileCopy.inProgress());
        when(gameFileRepository.getById(event.fileCopyNaturalId().gameFileId()))
                .thenReturn(gameFile);
        when(gameRepository.getById(gameFile.getGameId()))
                .thenReturn(TestGame.any());

        eventHandler.handle(event);

        var expectedJson = """
                {
                    "fileCopyWithContext": {
                      "fileCopy": {
                        "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                        "naturalId": {
                            "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                            "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                        },
                        "status": "IN_PROGRESS",
                        "failedReason": null,
                        "filePath": "someFilePath",
                        "dateCreated":"2022-04-29T14:15:53",
                        "dateModified":"2023-04-29T14:15:53"
                      },
                      "gameFile": {
                          "fileSource": {
                            "gameProviderId": "GOG",
                            "originalGameTitle": "Game 1",
                            "fileTitle": "Game 1 (Installer)",
                            "version": "1.0.0",
                            "url": "/downlink/some_game/some_file",
                            "originalFileName": "game_1_installer.exe",
                            "size": "5 KB"
                          }
                      },
                      "game": {
                        "title": "Test Game"
                      }
                    }
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination(), expectedJson);
    }
}