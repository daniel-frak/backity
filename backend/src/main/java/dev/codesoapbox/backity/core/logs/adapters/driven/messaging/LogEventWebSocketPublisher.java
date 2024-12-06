package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEvent;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LogEventWebSocketPublisher implements LogEventPublisher {

    private final LogCreatedWsEventMapper mapper;
    private final WebSocketEventPublisher eventPublisher;

    @Override
    public void publish(LogCreatedEvent event) {
        LogCreatedWsEvent payload = mapper.toWsEvent(event);
        eventPublisher.publish(LogWebSocketTopics.LOGS.wsDestination(), payload);
    }
}