package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFinishedRepositoryHandler implements DomainEventHandler<FileBackupFinishedEvent> {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @Override
    public Class<FileBackupFinishedEvent> getEventClass() {
        return FileBackupFinishedEvent.class;
    }

    @Override
    public void handle(FileBackupFinishedEvent event) {
        fileCopyReplicationProgressRepository.deleteByFileCopyId(event.fileCopyId());
    }
}
