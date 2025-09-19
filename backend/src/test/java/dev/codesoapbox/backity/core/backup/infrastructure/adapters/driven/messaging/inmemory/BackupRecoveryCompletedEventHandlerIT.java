package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.DomainEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@DomainEventHandlerTest
class BackupRecoveryCompletedEventHandlerIT {

    @Autowired
    private FileCopyReplicationProcess fileCopyReplicationProcessMock;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldNotifyFileCopyOfCompletedRecovery() {
        var event = new BackupRecoveryCompletedEvent();

        applicationEventPublisher.publishEvent(event);

        verify(fileCopyReplicationProcessMock).markBackupRecoveryCompleted();
    }
}