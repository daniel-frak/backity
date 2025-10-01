package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupStartedEventHandler {

    private final FileBackupStartedEventExternalForwarder eventForwarder;

    public void handle(FileBackupStartedEvent event) {
        eventForwarder.forward(event);
    }
}
