package dev.codesoapbox.backity.testing.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.testing.TestObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

public final class SimpMessagingAssertions {

    public static void assertSendsMessage(SimpMessagingTemplate simpMessagingTemplate,
                                    String expectedPayload, String topic, Runnable runnable)
            throws JsonProcessingException {
        AtomicReference<FileDiscoveryStatus> sentPayload = mockMessagesForTopic(simpMessagingTemplate, topic);

        runnable.run();

        assertNotNull(sentPayload.get());
        assertSameJson(TestObjectMapper.get(), expectedPayload, sentPayload.get());
    }

    private static <T> AtomicReference<T> mockMessagesForTopic(SimpMessagingTemplate simpMessagingTemplate, String topic) {
        AtomicReference<T> sentPayload = new AtomicReference<>();
        doAnswer(inv -> {
            sentPayload.set(inv.getArgument(1));
            return null;
        }).when(simpMessagingTemplate).convertAndSend(eq(topic), any(Object.class));
        return sentPayload;
    }

    private static void assertSameJson(ObjectMapper objectMapper,
                                String expectedPayload, Object sentPayload) throws JsonProcessingException {
        assertEquals(objectMapper.readTree(expectedPayload),
                objectMapper.readTree(objectMapper.writeValueAsString(sentPayload)));
    }
}
