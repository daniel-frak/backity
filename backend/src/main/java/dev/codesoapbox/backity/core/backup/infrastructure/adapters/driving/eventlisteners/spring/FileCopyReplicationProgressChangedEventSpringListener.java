package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileCopyReplicationProgressChangedEventSpringListener {

    private final FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @EventListener
    public void handle(FileCopyReplicationProgressChangedEvent event) {
        FileCopyReplicationProgress progress = toReplicationProgress(event);
        fileCopyReplicationProgressRepository.save(progress);
    }

    private FileCopyReplicationProgress toReplicationProgress(FileCopyReplicationProgressChangedEvent event) {
        return new FileCopyReplicationProgress(
                event.fileCopyId(),
                event.percentage(),
                event.timeLeft()
        );
    }
}
