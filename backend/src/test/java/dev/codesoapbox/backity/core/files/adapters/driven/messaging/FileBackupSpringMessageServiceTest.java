package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileBackupSpringMessageServiceTest {

    @InjectMocks
    private FileBackupSpringMessageService fileDownloadSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void shouldSendBackupStarted() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "id": 123,
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
                                123L,
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
                  "id": 123,
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
                                123L,
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