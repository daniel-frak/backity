package dev.codesoapbox.backity.core.shared.adapters.driven.spring;

import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class SpringMessageService implements MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }
}
