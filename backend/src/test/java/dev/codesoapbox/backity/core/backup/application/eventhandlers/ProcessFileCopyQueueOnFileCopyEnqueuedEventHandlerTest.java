package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.application.usecases.ProcessFileCopyQueueUseCase;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileCopyEnqueuedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessFileCopyQueueOnFileCopyEnqueuedEventHandlerTest {

    @Mock
    private ProcessFileCopyQueueUseCase useCase;

    private ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new ProcessFileCopyQueueOnFileCopyEnqueuedEventHandler(useCase);
    }

    @Test
    void shouldHandleEvent() {
        FileCopyEnqueuedEvent event = TestFileCopyEnqueuedEvent.any();

        eventHandler.handle(event);

        verify(useCase).processFileCopyQueue();
    }
}