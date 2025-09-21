package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.testing.messaging.WebSocketMessaging;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketPublisherTest;
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
class WebSocketEventPublisherIT {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebSocketEventPublisher eventPublisher;

    private WebSocketMessaging webSocketMessaging;
    private StompSession session;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketMessaging = new WebSocketMessaging();
        var stompClient = new WebSocketStompClient(new StandardWebSocketClient());
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

    @Test
    void shouldPublishWebSocketEvent() throws IOException {
        String topic = "/topic/test-topic";
        webSocketMessaging.subscribeTo(topic, session);

        eventPublisher.publish(topic, new TestPayload("testValue"));

        String receivedMessage = webSocketMessaging.receive(topic);
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