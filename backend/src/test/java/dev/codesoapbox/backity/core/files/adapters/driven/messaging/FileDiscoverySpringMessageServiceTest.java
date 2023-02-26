package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileDiscoverySpringMessageServiceTest {

    @InjectMocks
    private FileDiscoverySpringMessageService fileDiscoverySpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

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
                  "id": 1,
                  "source": "someSource",
                  "url": "someUrl",
                  "name": "someName",
                  "gameTitle": "someGameTitle",
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified": "+999999999-12-31T23:59:59.999999999",
                  "status": "DISCOVERED",
                  "failedReason": "someReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(),
                () -> fileDiscoverySpringMessageService.sendDiscoveredFile(
                        new GameFileVersion(
                                1L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFilePath",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileStatus.DISCOVERED,
                                "someReason"
                        )));
    }
}