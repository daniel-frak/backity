package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsMessageMapper;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class LogSpringMessageServiceTest {

    private LogSpringMessageService logSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        LogCreatedWsMessageMapper mapper = Mappers.getMapper(LogCreatedWsMessageMapper.class);
        logSpringMessageService = new LogSpringMessageService(mapper, simpMessagingTemplate);
    }

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