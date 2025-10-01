package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;

public interface FileBackupStartedEventExternalForwarder {

    void forward(FileBackupStartedEvent event);
}
