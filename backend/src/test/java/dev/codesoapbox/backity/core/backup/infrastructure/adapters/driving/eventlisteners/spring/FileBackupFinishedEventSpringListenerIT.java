package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class FileBackupFinishedEventSpringListenerIT {

    @Autowired
    private FileCopyReplicationProgressRepository fileCopyReplicationProgressRepositoryMock;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldHandleEvent() {
        FileBackupFinishedEvent event = TestFileBackupEvent.finishedIntegrityUnknown();

        applicationEventPublisher.publishEvent(event);

        verify(fileCopyReplicationProgressRepositoryMock).deleteByFileCopyId(event.fileCopyId());
    }
}