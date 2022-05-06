package dev.codesoapbox.backity.core.shared.adapters.driven.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpringMessageServiceTest {

    @InjectMocks
    private SpringMessageService springMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void shouldSendMessage() {
        springMessageService.sendMessage("someTopic", "somePayload");

        verify(simpMessagingTemplate).convertAndSend("someTopic", "somePayload");
    }
}