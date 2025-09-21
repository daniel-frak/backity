package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.testing.messaging.TestWebSocketMessaging;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketPublisherTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@WebSocketPublisherTest
class SpringWebSocketEventPublisherIT {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringWebSocketEventPublisher eventPublisher;

    private TestWebSocketMessaging testWebSocketMessaging;
    private StompSession session;
    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        testWebSocketMessaging = new TestWebSocketMessaging();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        session = connectToWebSocket(stompClient);
    }

    private StompSession connectToWebSocket(WebSocketStompClient stompClient)
            throws InterruptedException, ExecutionException, TimeoutException {
        return stompClient
                .connectAsync("ws://localhost:" + serverPort + WebSocketConfig.WS_ENDPOINT_SUFFIX,
                        new StompSessionHandlerAdapter() {
                        })
                .get(5, SECONDS);
    }

    @AfterEach
    void tearDown() {
        try {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        } finally {
            if (stompClient != null) {
                stompClient.stop();
            }
        }
    }

    @Test
    void shouldPublishWebSocketEvent() throws IOException {
        String topic = "/topic/test-topic";
        testWebSocketMessaging.subscribeTo(topic, session);

        eventPublisher.publish(topic, new TestPayload("testValue"));

        String receivedMessage = testWebSocketMessaging.receive(topic);
        String expectedMessage = """
                {
                    "value": "testValue"
                }
                """;
        assertSameJson(expectedMessage, receivedMessage);
    }

    private void assertSameJson(String expectedPayload, String sentPayload) throws JsonProcessingException {
        assertThat(objectMapper.readTree(sentPayload))
                .isEqualTo(objectMapper.readTree(expectedPayload));
    }

    private record TestPayload(String value) {
    }
}