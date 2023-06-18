package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredMessageMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressUpdateMessageMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileDiscoverySpringMessageServiceTest {

    private FileDiscoverySpringMessageService fileDiscoverySpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        FileDiscoveredMessageMapper fileDiscoveredMessageMapper = Mappers.getMapper(FileDiscoveredMessageMapper.class);
        FileDiscoveryStatusChangedMessageMapper fileDiscoveryStatusChangedMessageMapper =
                Mappers.getMapper(FileDiscoveryStatusChangedMessageMapper.class);
        FileDiscoveryProgressUpdateMessageMapper fileDiscoveryProgressUpdateMessageMapper =
                Mappers.getMapper(FileDiscoveryProgressUpdateMessageMapper.class);
        fileDiscoverySpringMessageService = new FileDiscoverySpringMessageService(simpMessagingTemplate,
                fileDiscoveredMessageMapper, fileDiscoveryStatusChangedMessageMapper,
                fileDiscoveryProgressUpdateMessageMapper);
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
                FileDiscoveryMessageTopics.FILE_DISCOVERY_STATUS_CHANGED.toString(),
                () -> fileDiscoverySpringMessageService.sendStatusChangedMessage(
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
                FileDiscoveryMessageTopics.FILE_DISCOVERY_PROGRESS_UPDATE.toString(),
                () -> fileDiscoverySpringMessageService.sendProgressUpdateMessage(new FileDiscoveryProgress(
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

        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDiscoveryMessageTopics.FILE_DISCOVERED.toString(),
                () -> fileDiscoverySpringMessageService.sendFileDiscoveredMessage(
                        gameFileDetails));
    }
}