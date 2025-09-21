package dev.codesoapbox.backity.testing.messaging;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class TestWebSocketMessaging {

    private static final String HANDSHAKE_MESSAGE = "subscription successful";
    public static final int TIMEOUT_SECONDS = 5;

    private final Map<String, Queue<String>> consumedEventsByQueueName = new ConcurrentHashMap<>();

    public void addMessage(String payload, String queueName) {
        consumedEventsByQueueName
                .computeIfAbsent(queueName, _ -> new ArrayBlockingQueue<>(1))
                .add(payload);
    }

    public String receive(String destination) {
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .until(() -> consumedEventsByQueueName.containsKey(destination)
                        && !consumedEventsByQueueName.get(destination).isEmpty());

        return getFirstMessage(destination);
    }

    private String getFirstMessage(String destination) {
        Queue<String> queue = consumedEventsByQueueName.get(destination);
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }

    public void discardHead(String destination) {
        getFirstMessage(destination);
    }

    public void subscribeTo(String destination, StompSession session) {
        session.subscribe(destination,
                new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return byte[].class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        String jsonPayload = new String((byte[]) payload, StandardCharsets.UTF_8);
                        addMessage(jsonPayload, destination);
                    }
                });

        waitForSuccessfulSubscription(destination, session);
    }

    private void waitForSuccessfulSubscription(String destination, StompSession session) {
        await()
                .atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .until(() -> subscriptionWasSuccessful(destination, session));
    }

    private boolean subscriptionWasSuccessful(String destination, StompSession session) {
        String head = peek(destination); // Do not consume non-handshake messages
        if (HANDSHAKE_MESSAGE.equals(head)) {
            discardHead(destination); // Remove the handshake message from the queue
            return true;
        }
        session.send(destination, HANDSHAKE_MESSAGE.getBytes(StandardCharsets.UTF_8));
        return false;
    }

    private String peek(String destination) {
        Queue<String> q = consumedEventsByQueueName.get(destination);
        return q != null ? q.peek() : null;
    }
}
