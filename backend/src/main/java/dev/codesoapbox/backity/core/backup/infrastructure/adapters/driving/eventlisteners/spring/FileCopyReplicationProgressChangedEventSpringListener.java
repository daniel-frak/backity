package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileCopyReplicationProgressChangedEventSpringListener {

    private final FileCopyReplicationProgressChangedEventHandler eventHandler;

    @EventListener
    public void handle(FileCopyReplicationProgressChangedEvent event) {
        eventHandler.handle(event);
    }
}
