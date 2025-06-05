package dev.codesoapbox.backity.core.backup.infrastructure.adapters.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDownloadProgressChangedRepositoryHandler
        implements DomainEventHandler<FileDownloadProgressChangedEvent> {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @Override
    public Class<FileDownloadProgressChangedEvent> getEventClass() {
        return FileDownloadProgressChangedEvent.class;
    }

    @Override
    public void handle(FileDownloadProgressChangedEvent event) {
        FileCopyReplicationProgress progress = toReplicationProgress(event);
        fileCopyReplicationProgressRepository.save(progress);
    }

    private FileCopyReplicationProgress toReplicationProgress(FileDownloadProgressChangedEvent event) {
        return new FileCopyReplicationProgress(
                event.fileCopyId(),
                event.percentage(),
                event.timeLeft()
        );
    }
}
