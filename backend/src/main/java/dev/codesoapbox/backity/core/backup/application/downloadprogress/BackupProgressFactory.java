package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class BackupProgressFactory {

    private final DomainEventPublisher domainEventPublisher;

    public BackupProgress create() {
        var progress = new BackupProgress();
        Consumer<ProgressInfo> consumer = progressInfo ->
                domainEventPublisher.publish(new FileBackupProgressChangedEvent(
                        progressInfo.percentage(), progressInfo.timeLeft().toSeconds()));

        progress.subscribeToProgress(consumer);

        return progress;
    }
}
