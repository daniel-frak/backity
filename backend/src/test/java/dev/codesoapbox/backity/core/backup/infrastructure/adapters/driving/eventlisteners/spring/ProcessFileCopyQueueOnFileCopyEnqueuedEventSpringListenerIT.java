package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileCopyEnqueuedEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import dev.codesoapbox.backity.testing.messaging.outbox.OutboxEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class ProcessFileCopyQueueOnFileCopyEnqueuedEventSpringListenerIT {

    @Autowired
    private ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler eventHandler;

    @Test
    void shouldHandleEvent(OutboxEventScenario scenario) {
        var event = TestFileCopyEnqueuedEvent.any();

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler).handle(event));
    }
}