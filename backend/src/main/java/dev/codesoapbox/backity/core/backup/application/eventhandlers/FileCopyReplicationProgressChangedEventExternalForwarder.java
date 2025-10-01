package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;

public interface FileCopyReplicationProgressChangedEventExternalForwarder {

    void forward(FileCopyReplicationProgressChangedEvent event);
}
