package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedMessageMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.inProgressFileDetails;
import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileBackupSpringMessageServiceTest {

    private FileBackupSpringMessageService fileDownloadSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        FileBackupStartedMessageMapper fileBackupStartedMessageMapper = Mappers.getMapper(FileBackupStartedMessageMapper.class);
        FileBackupStatusChangedMessageMapper fileBackupStatusChangedMessageMapper =
                Mappers.getMapper(FileBackupStatusChangedMessageMapper.class);
        fileDownloadSpringMessageService = new FileBackupSpringMessageService(simpMessagingTemplate,
                fileBackupStartedMessageMapper, fileBackupStatusChangedMessageMapper);
    }

    @Test
    void shouldSendBackupStartedMessage() throws JsonProcessingException {
        FileDetails fileDetails = inProgressFileDetails().build();

        var expectedPayload = """
                {
                    "fileDetailsId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "originalGameTitle": "someOriginalGameTitle",
                    "fileTitle": "someFileTitle",
                    "version": "someVersion",
                    "originalFileName": "someOriginalFileName",
                    "size": "5 KB",
                    "filePath": "tempFilePath"
                }
                """;
        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.BACKUP_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupStarted(fileDetails));
    }

    @Test
    void shouldSendBackupProgressUpdateMessage() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "percentage": 25,
                  "timeLeftSeconds": 1234
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.BACKUP_PROGRESS_UPDATE.toString(),
                () -> fileDownloadSpringMessageService.sendProgress(
                        new FileBackupProgress(
                                25,
                                1234L
                        )));
    }

    @Test
    void shouldSendBackupStatusChangedMessage() throws JsonProcessingException {
        FileDetails fileDetails = fullFileDetails().build();

        var expectedPayload = """
                {
                    "fileDetailsId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "newStatus": "DISCOVERED",
                    "failedReason": "someFailedReason"
                }
                """;
        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupMessageTopics.BACKUP_STATUS_CHANGED.toString(),
                () -> fileDownloadSpringMessageService.sendBackupFinished(
                        fileDetails));
    }
}