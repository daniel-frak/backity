package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.*;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteFileCopyUseCaseTest {

    private DeleteFileCopyUseCase useCase;

    private FakeUnixStorageSolution storageSolution;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        useCase = new DeleteFileCopyUseCase(storageSolution, gameFileRepository);
    }

    @Test
    void shouldDeleteFileCopyGivenGameFileStatusIsSuccess() {
        GameFile gameFile = mockSuccessfulGameFileExists();
        String filePath = gameFile.getFileCopy().getFilePath();

        useCase.deleteFileCopy(gameFile.getId());

        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
    }

    private GameFile mockSuccessfulGameFileExists() {
        GameFile gameFile = TestGameFile.successful();
        when(gameFileRepository.getById(gameFile.getId()))
                .thenReturn(gameFile);
        return gameFile;
    }

    @Test
    void shouldChangeStatusOfGameFileWhenDeletingFile() {
        GameFile gameFile = mockSuccessfulGameFileExists();

        useCase.deleteFileCopy(gameFile.getId());

        assertThat(gameFile.getFileCopy().getStatus()).isEqualTo(FileBackupStatus.DISCOVERED);
        verify(gameFileRepository).save(gameFile);
    }

    @Test
    void shouldNotChangeStatusOfGameFileGivenFileDeletionFailed() {
        var exception = new RuntimeException("test");
        storageSolution.setShouldThrowOnFileDeletion(exception);
        GameFile gameFile = mockSuccessfulGameFileExists();

        assertThatThrownBy(() -> useCase.deleteFileCopy(gameFile.getId()))
                .isSameAs(exception);

        assertThat(gameFile.getFileCopy().getStatus()).isNotEqualTo(FileBackupStatus.DISCOVERED);
        verify(gameFileRepository, never()).save(any());
    }

    @Test
    void shouldThrowGivenGameFileStatusIsNotSuccess() {
        GameFile gameFile = mockEnqueuedGameFileExists();
        GameFileId gameFileId = gameFile.getId();

        assertThatThrownBy(() -> useCase.deleteFileCopy(gameFileId))
                .isInstanceOf(GameFileNotBackedUpException.class)
                .hasMessageContaining(gameFileId.toString());
        assertThat(storageSolution.anyFileDeleteWasAttempted()).isFalse();
    }

    private GameFile mockEnqueuedGameFileExists() {
        GameFile gameFile = TestGameFile.enqueued();
        when(gameFileRepository.getById(gameFile.getId()))
                .thenReturn(gameFile);
        return gameFile;
    }
}