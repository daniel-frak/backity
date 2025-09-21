package dev.codesoapbox.backity.testing.messaging;

import lombok.SneakyThrows;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class WebSocketMessaging {

    private final Map<String, Queue<String>> consumedEventsByQueueName = synchronizedMap(new HashMap<>());

    public void addMessage(String payload, String queueName) {
        consumedEventsByQueueName
                .computeIfAbsent(queueName, _ -> new ArrayBlockingQueue<>(1))
                .add(payload);
    }

    public String receive(String destination) {
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> consumedEventsByQueueName.containsKey(destination)
                        && !consumedEventsByQueueName.get(destination).isEmpty());

        return getFirstMessage(destination);
    }

    public String poll(String destination) {
        return getFirstMessage(destination);
    }

    private String getFirstMessage(String destination) {
        Queue<String> queue = consumedEventsByQueueName.get(destination);
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }

    @SneakyThrows
    public void subscribeTo(String destination, StompSession session) {
        session.subscribe(destination,
                new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Object.class;
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
        String handshakeMessage = "subscription successful";
        await()
                .atMost(5, SECONDS)
                .until(() -> {
                    String msg = poll(destination);
                    if (msg != null) {
                        return true;
                    }
                    session.send(destination, handshakeMessage.getBytes(StandardCharsets.UTF_8));
                    return false;
                });
    }
}
