package dev.codesoapbox.backity.core.gamefile.application.usecases;

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
    void shouldSetFileBackupStatusToEnqueuedAndPersistGameFile() {
        GameFile gameFile = mockDiscoveredGameFileExists();

        useCase.enqueue(gameFile.getId());

        assertThat(gameFile.getFileCopy().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
        verify(gameFileRepository).save(gameFile);
    }

    private GameFile mockDiscoveredGameFileExists() {
        GameFile gameFile = TestGameFile.discovered();
        when(gameFileRepository.getById(gameFile.getId()))
                .thenReturn(gameFile);

        return gameFile;
    }
}