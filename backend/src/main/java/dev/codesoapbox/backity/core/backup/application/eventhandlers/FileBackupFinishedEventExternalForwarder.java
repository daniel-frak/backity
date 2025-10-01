package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;

public interface FileBackupFinishedEventExternalForwarder {

    void forward(FileBackupFinishedEvent event);
}
