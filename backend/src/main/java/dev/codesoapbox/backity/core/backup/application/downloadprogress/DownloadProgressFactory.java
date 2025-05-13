package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class DownloadProgressFactory {

    private final DomainEventPublisher domainEventPublisher;

    public DownloadProgress create() {
        var progress = new DownloadProgress();
        Consumer<ProgressInfo> consumer = progressInfo ->
                domainEventPublisher.publish(new FileDownloadProgressChangedEvent(
                        progressInfo.percentage(), progressInfo.timeLeft().toSeconds()));

        progress.subscribeToProgress(consumer);

        return progress;
    }
}
