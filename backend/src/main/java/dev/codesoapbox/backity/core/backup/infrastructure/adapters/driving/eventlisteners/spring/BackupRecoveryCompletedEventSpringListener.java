package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
public class BackupRecoveryCompletedEventSpringListener {

    private final BackupRecoveryCompletedEventHandler eventHandler;

    // @TODO Should listen to outbox event
    // @TODO Test if we need an ArchUnit test for unique event listener ids
    @Async
    @TransactionalEventListener(
            value = BackupRecoveryCompletedEvent.class,
            id = "backup-recovery-completed-spring-listener"
    )
    public void listen() {
        eventHandler.handle();
    }
}
