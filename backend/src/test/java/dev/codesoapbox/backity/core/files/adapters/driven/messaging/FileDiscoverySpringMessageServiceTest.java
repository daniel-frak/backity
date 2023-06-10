package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessageMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
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
class FileDiscoverySpringMessageServiceTest {

    private FileDiscoverySpringMessageService fileDiscoverySpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        fileDiscoverySpringMessageService = new FileDiscoverySpringMessageService(simpMessagingTemplate,
                GameFileDetailsMessageMapper.INSTANCE);
    }

    @Test
    void shouldSendStatus() throws JsonProcessingException {
        var expectedPayload = """
                {
                    "source": "someSource",
                    "inProgress" : true
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERY_STATUS.toString(),
                () -> fileDiscoverySpringMessageService.sendStatus(
                        new FileDiscoveryStatus("someSource", true)));
    }


    @Test
    void shouldSendProgress() throws JsonProcessingException {
        var expectedPayload = """
                {
                    "source": "someSource",
                    "percentage" : 25,
                    "timeLeftSeconds": 123
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERY_PROGRESS.toString(),
                () -> fileDiscoverySpringMessageService.sendProgress(new FileDiscoveryProgress(
                        "someSource", 25, 123)));
    }

    @Test
    void shouldSendDiscoveredFile() throws JsonProcessingException {
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
                  "dateModified": "+999999999-12-31T23:59:59.999999999",
                  "backupStatus": "DISCOVERED",
                  "backupFailedReason": "someReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(),
                () -> fileDiscoverySpringMessageService.sendDiscoveredFile(
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
                                FileBackupStatus.DISCOVERED,
                                "someReason"
                        )));
    }
}