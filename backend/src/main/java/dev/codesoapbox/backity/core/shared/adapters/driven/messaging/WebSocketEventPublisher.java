package dev.codesoapbox.backity.core.shared.adapters.driven.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class WebSocketEventPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publish(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }
}