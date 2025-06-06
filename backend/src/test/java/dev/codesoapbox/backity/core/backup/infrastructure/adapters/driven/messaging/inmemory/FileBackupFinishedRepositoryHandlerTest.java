package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileBackupFinishedRepositoryHandlerTest {

    private FileBackupFinishedRepositoryHandler handler;

    @Mock
    private FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @BeforeEach
    void setUp() {
        handler = new FileBackupFinishedRepositoryHandler(fileCopyReplicationProgressRepository);
    }

    @Test
    void shouldGetEventClass() {
        Class<FileBackupFinishedEvent> result = handler.getEventClass();

        assertThat(result).isEqualTo(FileBackupFinishedEvent.class);
    }

    @Test
    void shouldHandleEvent() {
        FileBackupFinishedEvent event = TestFileBackupEvent.finished();

        handler.handle(event);

        verify(fileCopyReplicationProgressRepository).deleteByFileCopyId(event.fileCopyId());
    }
}