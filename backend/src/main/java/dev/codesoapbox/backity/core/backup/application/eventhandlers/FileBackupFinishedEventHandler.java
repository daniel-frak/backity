package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFinishedEventHandler {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    public void handle(FileBackupFinishedEvent event) {
        fileCopyReplicationProgressRepository.deleteByFileCopyId(event.fileCopyId());
    }
}
