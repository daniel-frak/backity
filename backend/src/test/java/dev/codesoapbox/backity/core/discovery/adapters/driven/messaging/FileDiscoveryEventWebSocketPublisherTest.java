package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileDiscoveryEventWebSocketPublisherTest {

    private FileDiscoveryEventWebSocketPublisher fileDiscoveryEventWebSocketPublisher;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        FileDiscoveredWsEventMapper fileDiscoveredWsEventMapper = Mappers.getMapper(FileDiscoveredWsEventMapper.class);
        FileDiscoveryStatusChangedWsEventMapper fileDiscoveryStatusChangedWsEventMapper =
                Mappers.getMapper(FileDiscoveryStatusChangedWsEventMapper.class);
        FileDiscoveryProgressChangedWsEventMapper fileDiscoveryProgressChangedWsEventMapper =
                Mappers.getMapper(FileDiscoveryProgressChangedWsEventMapper.class);
        fileDiscoveryEventWebSocketPublisher = new FileDiscoveryEventWebSocketPublisher(simpMessagingTemplate,
                fileDiscoveredWsEventMapper, fileDiscoveryStatusChangedWsEventMapper,
                fileDiscoveryProgressChangedWsEventMapper);
    }

    @Test
    void shouldSendStatus() throws JsonProcessingException {
        var expectedPayload = """
                {
                    "source": "someSource",
                    "isInProgress" : true
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryWebSocketTopics.FILE_DISCOVERY_STATUS_CHANGED.toString(),
                () -> fileDiscoveryEventWebSocketPublisher.publishStatusChangedEvent(
                        new FileDiscoveryStatusChangedEvent("someSource", true)));
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
                FileDiscoveryWebSocketTopics.FILE_DISCOVERY_PROGRESS_UPDATE.toString(),
                () -> fileDiscoveryEventWebSocketPublisher.publishProgressChangedEvent(new FileDiscoveryProgressChangedEvent(
                        "someSource", 25, 123)));
    }

    @Test
    void shouldSendDiscoveredFile() throws JsonProcessingException {
        var expectedPayload = """
                {
                    "originalGameTitle": "someOriginalGameTitle",
                    "originalFileName": "someOriginalFileName",
                    "fileTitle": "someFileTitle",
                    "size": "5 KB"
                }
                """;

        FileDetails fileDetails = discoveredFileDetails().build();

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryWebSocketTopics.FILE_DISCOVERED.toString(),
                () -> fileDiscoveryEventWebSocketPublisher.publishFileDiscoveredEvent(
                        fileDetails));
    }
}