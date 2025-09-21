package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpringDomainEventPublisherTest {

    private SpringDomainEventPublisher domainEventPublisher;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        domainEventPublisher = new SpringDomainEventPublisher(applicationEventPublisher);
    }

    @Test
    void shouldPublishEvent() {
        var event = new TestDomainEvent();

        domainEventPublisher.publish(event);

        verify(applicationEventPublisher, times(1)).publishEvent(event);
    }

    static class TestDomainEvent implements DomainEvent {
    }
}
