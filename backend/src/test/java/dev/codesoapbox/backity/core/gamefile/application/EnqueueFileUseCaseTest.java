package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnqueueFileUseCaseTest {

    private EnqueueFileUseCase useCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new EnqueueFileUseCase(gameFileRepository);
    }

    @Test
    void shouldEnqueue() {
        var id = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
        GameFile gameFile = TestGameFile.discoveredGameFile().build();
        when(gameFileRepository.getById(id))
                .thenReturn(gameFile);

        useCase.enqueue(id);

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
        verify(gameFileRepository).save(gameFile);
    }
}