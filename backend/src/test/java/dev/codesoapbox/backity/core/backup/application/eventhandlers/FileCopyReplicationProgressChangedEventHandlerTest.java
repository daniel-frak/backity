package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileCopyReplicationProgressChangedEventHandlerTest {

    @Mock
    private FileCopyReplicationProgressRepository replicationProgressRepository;

    private FileCopyReplicationProgressChangedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new FileCopyReplicationProgressChangedEventHandler(
                replicationProgressRepository);
    }

    @Test
    void shouldHandle() {
        FileCopyReplicationProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        eventHandler.handle(event);

        var expectedReplicationProgress = new FileCopyReplicationProgress(
                event.fileCopyId(), event.percentage(), event.timeLeft());
        verify(replicationProgressRepository).save(expectedReplicationProgress);
    }
}