package dev.codesoapbox.backity.core.backup.application.writeprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class OutputStreamProgressTrackerFactory {

    private final DomainEventPublisher domainEventPublisher;

    public OutputStreamProgressTracker create(FileCopy fileCopy) {
        var outputStreamProgressTracker = new OutputStreamProgressTracker();
        Consumer<ProgressInfo> consumer = progressInfo ->
                domainEventPublisher.publish(new FileCopyReplicationProgressChangedEvent(
                        fileCopy.getId(), fileCopy.getNaturalId(), progressInfo.percentage(), progressInfo.timeLeft()));

        outputStreamProgressTracker.subscribeToProgress(consumer);

        return outputStreamProgressTracker;
    }
}
