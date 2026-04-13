package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ClearProgressOnFileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class ClearProgressOnFileBackupFinishedEventSpringListener {

    private final ClearProgressOnFileBackupFinishedEventHandler eventHandler;

    // Event handling happens completely in-memory, so outbox is not needed
    @Async
    @EventListener
    public void listen(FileBackupFinishedEvent event) {
        eventHandler.handle(event);
    }
}
