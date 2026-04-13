package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListener {

    private final MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler eventHandler;

    // Event handling happens completely in-memory, so outbox is not needed
    @Async
    @EventListener(BackupRecoveryCompletedEvent.class)
    public void listen() {
        eventHandler.handle();
    }
}
