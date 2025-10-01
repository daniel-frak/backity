package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFailedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFailedEventSpringListener {

    private final FileBackupFailedEventHandler eventHandler;

    @EventListener
    public void handle(FileBackupFailedEvent event) {
        eventHandler.handle(event);
    }
}