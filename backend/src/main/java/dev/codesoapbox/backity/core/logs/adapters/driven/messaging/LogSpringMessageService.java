package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsMessage;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsMessageMapper;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import dev.codesoapbox.backity.core.logs.domain.services.LogMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class LogSpringMessageService implements LogMessageService {

    private final LogCreatedWsMessageMapper mapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendLogCreated(LogCreatedMessage message) {
        LogCreatedWsMessage payload = mapper.toWsMessage(message);
        sendMessage(LogsMessageTopics.LOGS.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }
}