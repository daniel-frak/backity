package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessageMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileBackupSpringMessageServiceTest {

    private FileBackupSpringMessageService fileDownloadSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        fileDownloadSpringMessageService = new FileBackupSpringMessageService(simpMessagingTemplate,
                GameFileDetailsMessageMapper.INSTANCE);
    }

    @Test
    void shouldSendBackupStarted() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                  "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                  "sourceFileDetails": {
                    "sourceId": "someSourceId1",
                    "originalGameTitle": "someOriginalGameTitle1",
                    "fileTitle": "someFileTitle1",
                    "version": "someVersion1",
                    "url": "someUrl1",
                    "originalFileName": "someOriginalFileName1",
                    "size": "5 KB"
                  },
                  "backupDetails": {
                    "status": "ENQUEUED",
                    "failedReason": null,
                    "filePath": null
                  },
                  "dateCreated": "2022-04-29T14:15:53",
                  "dateModified": "2023-04-29T14:15:53"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupStarted(
                        TestGameFileDetails.GAME_FILE_DETAILS_1.get()));
    }

    @Test
    void shouldSendBackupProgress() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "percentage": 25,
                  "timeLeftSeconds": 1234
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_PROGRESS.toString(),
                () -> fileDownloadSpringMessageService.sendProgress(
                        new FileBackupProgress(
                                25,
                                1234L
                        )));
    }

    @Test
    void shouldSendBackupFinished() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                  "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                  "sourceFileDetails": {
                    "sourceId": "someSourceId1",
                    "originalGameTitle": "someOriginalGameTitle1",
                    "fileTitle": "someFileTitle1",
                    "version": "someVersion1",
                    "url": "someUrl1",
                    "originalFileName": "someOriginalFileName1",
                    "size": "5 KB"
                  },
                  "backupDetails": {
                    "status": "ENQUEUED",
                    "failedReason": null,
                    "filePath": null
                  },
                  "dateCreated": "2022-04-29T14:15:53",
                  "dateModified": "2023-04-29T14:15:53"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_FINISHED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupFinished(
                        TestGameFileDetails.GAME_FILE_DETAILS_1.get()));
    }
}