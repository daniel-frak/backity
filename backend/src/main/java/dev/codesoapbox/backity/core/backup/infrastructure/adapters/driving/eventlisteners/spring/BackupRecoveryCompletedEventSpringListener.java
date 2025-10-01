package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class BackupRecoveryCompletedEventSpringListener {

    private final BackupRecoveryCompletedEventHandler eventHandler;

    @EventListener(BackupRecoveryCompletedEvent.class)
    public void handle() {
        eventHandler.handle();
    }
}
