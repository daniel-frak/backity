package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class LogSpringMessageServiceTest {

    @InjectMocks
    private LogSpringMessageService logSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void shouldSendLogCreated() throws JsonProcessingException {
        var expectedPayload = """
                {
                    "message": "someMessage",
                    "maxLogs" : 123
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                LogsMessageTopics.LOGS.toString(),
                () -> logSpringMessageService.sendLogCreated(
                        LogCreatedMessage.of("someMessage", 123)));
    }
}