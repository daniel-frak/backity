package dev.codesoapbox.backity.core.files.discovery.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

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
                  "id": {
                    "url": "someUrl",
                    "version": "someVersion"
                  },
                  "uniqueId": "8bb1e5cc-a6ce-4c6a-8a02-edb5576e3afd",
                  "source": "someSource",
                  "name": "someName",
                  "gameTitle": "someGameTitle",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "dateModified": "+999999999-12-31T23:59:59.999999999",
                  "enqueued": true,
                  "ignored": false
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(),
                () -> fileDiscoverySpringMessageService.sendDiscoveredFile(
                        new DiscoveredFile(
                                new DiscoveredFileId("someUrl", "someVersion"),
                                UUID.fromString("8bb1e5cc-a6ce-4c6a-8a02-edb5576e3afd"),
                                "someSource",
                                "someName",
                                "someGameTitle",
                                "someSize",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                true,
                                false
                        )));
    }
}