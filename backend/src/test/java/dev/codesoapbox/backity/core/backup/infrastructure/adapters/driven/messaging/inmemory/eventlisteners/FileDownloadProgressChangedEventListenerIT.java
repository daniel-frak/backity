package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.DomainEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@DomainEventHandlerTest
class FileDownloadProgressChangedEventListenerIT {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FileCopyReplicationProgressRepository replicationProgressRepositoryMock;

    @Test
    void shouldHandle() {
        FileDownloadProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        applicationEventPublisher.publishEvent(event);

        var expectedReplicationProgress = new FileCopyReplicationProgress(
                event.fileCopyId(), event.percentage(), event.timeLeft());
        verify(replicationProgressRepositoryMock).save(expectedReplicationProgress);
    }
}