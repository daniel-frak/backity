package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
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
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "status": "IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupStarted(
                        new GameFileVersionBackup(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFileName",
                                "someFilePath",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
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
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "status": "IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.DOWNLOAD_FINISHED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupFinished(
                        new GameFileVersionBackup(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFileName",
                                "someFilePath",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileBackupStatus.IN_PROGRESS,
                                "someFailedReason"
                        )));
    }
}