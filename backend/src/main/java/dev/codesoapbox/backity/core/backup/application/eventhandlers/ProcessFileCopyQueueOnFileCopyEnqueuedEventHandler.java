package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.application.usecases.ProcessFileCopyQueueUseCase;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler {

    private final ProcessFileCopyQueueUseCase useCase;

    @SuppressWarnings({"java:S1172", "unused"})
    public void handle(FileCopyEnqueuedEvent event) {
        useCase.processFileCopyQueue();
    }
}
