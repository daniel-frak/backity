package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListener {

    private final SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler eventHandler;

    // Event handling happens completely in-memory, so outbox is not needed.
    // Additionally, this event gets created very often, so it would be a significant performance burden to outbox it.
    @Async
    @EventListener
    public void listen(FileCopyReplicationProgressChangedEvent event) {
        eventHandler.handle(event);
    }
}
