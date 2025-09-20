package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFinishedRepositoryHandler {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @EventListener
    public void handle(FileBackupFinishedEvent event) {
        fileCopyReplicationProgressRepository.deleteByFileCopyId(event.fileCopyId());
    }
}
