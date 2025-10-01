package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupStartedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupStartedEventSpringListener {

    private final FileBackupStartedEventHandler eventHandler;

    @EventListener
    public void handle(FileBackupStartedEvent event) {
        eventHandler.handle(event);
    }
}