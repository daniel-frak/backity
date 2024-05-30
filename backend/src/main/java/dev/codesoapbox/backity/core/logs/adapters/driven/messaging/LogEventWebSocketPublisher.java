package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEvent;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class LogEventWebSocketPublisher implements LogEventPublisher {

    private final LogCreatedWsEventMapper mapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void publish(LogCreatedEvent event) {
        LogCreatedWsEvent payload = mapper.toWsEvent(event);
        publish(LogWebSocketTopics.LOGS.toString(), payload);
    }

    private void publish(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }
}