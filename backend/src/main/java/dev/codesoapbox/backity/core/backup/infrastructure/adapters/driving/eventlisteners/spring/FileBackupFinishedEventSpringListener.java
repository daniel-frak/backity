package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFinishedEventSpringListener {

    private final FileBackupFinishedEventHandler eventHandler;

    @EventListener
    public void handle(FileBackupFinishedEvent event) {
        eventHandler.handle(event);
    }
}
