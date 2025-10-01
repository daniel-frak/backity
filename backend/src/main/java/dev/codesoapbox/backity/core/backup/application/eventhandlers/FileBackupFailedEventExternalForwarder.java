package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;

public interface FileBackupFailedEventExternalForwarder {

    void forward(FileBackupFailedEvent event);
}
