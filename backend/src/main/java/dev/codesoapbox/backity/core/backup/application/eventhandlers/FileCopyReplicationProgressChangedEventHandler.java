package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileCopyReplicationProgressChangedEventHandler {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;
    private final FileCopyReplicationProgressChangedEventExternalForwarder eventForwarder;

    public void handle(FileCopyReplicationProgressChangedEvent event) {
        // @TODO Handle independently:
        FileCopyReplicationProgress progress = toReplicationProgress(event);
        fileCopyReplicationProgressRepository.save(progress);
        eventForwarder.forward(event);
    }

    private FileCopyReplicationProgress toReplicationProgress(FileCopyReplicationProgressChangedEvent event) {
        return new FileCopyReplicationProgress(
                event.fileCopyId(),
                event.percentage(),
                event.timeLeft()
        );
    }
}
