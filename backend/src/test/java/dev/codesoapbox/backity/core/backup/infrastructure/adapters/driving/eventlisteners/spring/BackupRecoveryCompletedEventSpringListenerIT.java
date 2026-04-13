package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

// @TODO More event listeners must be converted (and tests updated)
@SpringEventListenerTest
class BackupRecoveryCompletedEventSpringListenerIT {

    @Autowired
    private BackupRecoveryCompletedEventHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldHandleEvent() {
        var event = new BackupRecoveryCompletedEvent();

        new TransactionTemplate(transactionManager)
                .executeWithoutResult(_ -> applicationEventPublisher.publishEvent(event));

        await()
                .untilAsserted(() -> verify(eventHandler).handle());
    }
}