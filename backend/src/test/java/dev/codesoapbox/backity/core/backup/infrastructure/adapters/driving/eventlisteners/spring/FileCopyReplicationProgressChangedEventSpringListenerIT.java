package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class FileCopyReplicationProgressChangedEventSpringListenerIT {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FileCopyReplicationProgressRepository replicationProgressRepositoryMock;

    @Test
    void shouldHandle() {
        FileCopyReplicationProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        applicationEventPublisher.publishEvent(event);

        var expectedReplicationProgress = new FileCopyReplicationProgress(
                event.fileCopyId(), event.percentage(), event.timeLeft());
        verify(replicationProgressRepositoryMock).save(expectedReplicationProgress);
    }
}