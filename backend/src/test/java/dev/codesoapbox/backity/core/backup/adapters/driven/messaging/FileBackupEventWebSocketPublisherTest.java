package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
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
class FileBackupEventWebSocketPublisherTest {

    private FileBackupEventWebSocketPublisher fileBackupEventWebSocketPublisher;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        FileBackupStartedWsEventMapper backupStartedEventMapper =
                Mappers.getMapper(FileBackupStartedWsEventMapper.class);
        FileBackupProgressUpdatedWsEventMapper progressUpdatedEventMapper =
                Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);
        FileBackupStatusChangedWsEventMapper statusChangedEventMapper =
                Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);
        fileBackupEventWebSocketPublisher = new FileBackupEventWebSocketPublisher(simpMessagingTemplate,
                backupStartedEventMapper, progressUpdatedEventMapper, statusChangedEventMapper);
    }

    @Test
    void shouldPublishBackupStartedEvent() throws JsonProcessingException {
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
                FileBackupWebSocketTopics.BACKUP_STARTED.toString(),
                () -> fileBackupEventWebSocketPublisher.publishBackupStartedEvent(fileDetails));
    }

    @Test
    void shouldPublishBackupProgressChangedEvent() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "percentage": 25,
                  "timeLeftSeconds": 1234
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupWebSocketTopics.BACKUP_PROGRESS_UPDATE.toString(),
                () -> fileBackupEventWebSocketPublisher.publishFileBackupProgressChangedEvent(
                        new FileBackupProgress(
                                25,
                                1234L
                        )));
    }

    @Test
    void shouldPublishBackupFinishedEvent() throws JsonProcessingException {
        FileDetails fileDetails = fullFileDetails().build();

        var expectedPayload = """
                {
                    "fileDetailsId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                    "newStatus": "DISCOVERED",
                    "failedReason": "someFailedReason"
                }
                """;
        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.toString(),
                () -> fileBackupEventWebSocketPublisher.publishBackupFinishedEvent(
                        fileDetails));
    }
}