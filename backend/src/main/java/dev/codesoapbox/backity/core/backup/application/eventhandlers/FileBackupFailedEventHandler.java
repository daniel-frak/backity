package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFailedEventHandler {

    private final FileBackupFailedEventExternalForwarder eventForwarder;

    public void handle(FileBackupFailedEvent event) {
        eventForwarder.forward(event);
    }
}
