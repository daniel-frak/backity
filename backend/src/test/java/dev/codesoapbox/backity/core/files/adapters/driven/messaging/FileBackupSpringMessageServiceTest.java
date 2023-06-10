package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessageMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

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
                  "source": "someSource",
                  "url": "someUrl",
                  "title": "someName",
                  "originalFileName":"someFileName",
                  "filePath":"someFilePath",
                  "gameTitle": "someGameTitle",
                  "gameId": "someGameId",
                  "version": "someVersion",
                  "size": "100 KB",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "backupStatus": "IN_PROGRESS",
                  "backupFailedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupStarted(
                        new GameFileDetails(
                                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFileName",
                                "someFilePath",
                                "someGameTitle",
                                "someGameId",
                                "someVersion",
                                "100 KB",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileBackupStatus.IN_PROGRESS,
                                "someFailedReason"
                        )));
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
                  "source": "someSource",
                  "url": "someUrl",
                  "title": "someName",
                  "originalFileName":"someFileName",
                  "filePath":"someFilePath",
                  "gameTitle": "someGameTitle",
                  "gameId": "someGameId",
                  "version": "someVersion",
                  "size": "100 KB",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "backupStatus": "IN_PROGRESS",
                  "backupFailedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_FINISHED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupFinished(
                        new GameFileDetails(
                                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFileName",
                                "someFilePath",
                                "someGameTitle",
                                "someGameId",
                                "someVersion",
                                "100 KB",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileBackupStatus.IN_PROGRESS,
                                "someFailedReason"
                        )));
    }
}