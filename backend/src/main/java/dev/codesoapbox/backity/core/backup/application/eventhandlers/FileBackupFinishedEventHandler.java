package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFinishedEventHandler {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;
    private final FileBackupFinishedEventExternalForwarder eventForwarder;

    public void handle(FileBackupFinishedEvent event) {
        // @TODO Handle independently:
        fileCopyReplicationProgressRepository.deleteByFileCopyId(event.fileCopyId());
        eventForwarder.forward(event);
    }
}
