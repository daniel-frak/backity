package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
public class ProcessFileCopyQueueOnFileCopyEnqueuedEventSpringListener {

    private final ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler eventHandler;

    // Must be outboxed so that queue will be reprocessed on restart
    @Async
    @TransactionalEventListener(id = "process-file-copy-queue-on-file-copy-enqueued-event")
    public void listen(FileCopyEnqueuedEvent event) {
        eventHandler.handle(event);
    }
}
