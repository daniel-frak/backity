package dev.codesoapbox.backity.testing.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedMap;
import static org.apache.commons.collections4.QueueUtils.emptyQueue;
import static org.awaitility.Awaitility.await;

public class WebSocketMessaging {

    private final Map<String, Queue<String>> consumedEventsByQueueName = synchronizedMap(new HashMap<>());

    public void addMessage(String payload, String queueName) {
        consumedEventsByQueueName.computeIfAbsent(queueName, key -> new ArrayBlockingQueue<>(1))
                .add(payload);
    }

    public String receive(String destination) {
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !consumedEventsByQueueName.isEmpty());

        return getFirstMessage(destination);
    }

    private String getFirstMessage(String destination) {
        return consumedEventsByQueueName.getOrDefault(destination, emptyQueue()).poll();
    }
}
