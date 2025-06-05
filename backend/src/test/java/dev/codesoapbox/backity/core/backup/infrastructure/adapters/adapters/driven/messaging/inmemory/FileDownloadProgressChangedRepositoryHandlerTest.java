package dev.codesoapbox.backity.core.backup.infrastructure.adapters.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileDownloadProgressChangedRepositoryHandlerTest {

    private FileDownloadProgressChangedRepositoryHandler handler;

    @Mock
    private FileCopyReplicationProgressRepository replicationProgressRepository;

    @BeforeEach
    void setUp() {
        handler = new FileDownloadProgressChangedRepositoryHandler(replicationProgressRepository);
    }

    @Test
    void shouldGetEventClass() {
        Class<FileDownloadProgressChangedEvent> result = handler.getEventClass();

        assertThat(result).isEqualTo(FileDownloadProgressChangedEvent.class);
    }

    @Test
    void shouldHandle() {
        FileDownloadProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        handler.handle(event);

        var expectedReplicationProgress = new FileCopyReplicationProgress(event.fileCopyId(),
                50, Duration.ofSeconds(999));
        verify(replicationProgressRepository).save(expectedReplicationProgress);
    }
}