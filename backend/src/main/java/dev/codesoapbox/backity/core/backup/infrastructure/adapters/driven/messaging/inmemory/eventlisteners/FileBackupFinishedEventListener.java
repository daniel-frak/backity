package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFinishedEventListener {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @EventListener
    public void handle(FileBackupFinishedEvent event) {
        fileCopyReplicationProgressRepository.deleteByFileCopyId(event.fileCopyId());
    }
}
