package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileDownloadProgressChangedEventListener {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @EventListener
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
