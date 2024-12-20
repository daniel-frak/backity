package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
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
class DeleteFileUseCaseTest {

    private DeleteFileUseCase useCase;

    private FakeUnixFileManager fileManager;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        fileManager = new FakeUnixFileManager();
        useCase = new DeleteFileUseCase(fileManager, gameFileRepository);
    }

    @Test
    void shouldDeleteFileGivenGameFileStatusIsSuccess() {
        GameFile gameFile = mockSuccessfulGameFileExists();
        String filePath = gameFile.getFileBackup().getFilePath();

        useCase.deleteFile(gameFile.getId());

        assertThat(fileManager.fileDeleteWasAttempted(filePath)).isTrue();
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

        useCase.deleteFile(gameFile.getId());

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.DISCOVERED);
        verify(gameFileRepository).save(gameFile);
    }

    @Test
    void shouldNotChangeStatusOfGameFileGivenFileDeletionFailed() {
        var exception = new RuntimeException("test");
        fileManager.setShouldThrowOnFileDeletion(exception);
        GameFile gameFile = mockSuccessfulGameFileExists();

        assertThatThrownBy(() -> useCase.deleteFile(gameFile.getId()))
                .isSameAs(exception);

        assertThat(gameFile.getFileBackup().getStatus()).isNotEqualTo(FileBackupStatus.DISCOVERED);
        verify(gameFileRepository, never()).save(any());
    }

    @Test
    void shouldThrowGivenGameFileStatusIsNotSuccess() {
        GameFile gameFile = mockEnqueuedGameFileExists();
        GameFileId gameFileId = gameFile.getId();

        assertThatThrownBy(() -> useCase.deleteFile(gameFileId))
                .isInstanceOf(GameFileNotBackedUpException.class)
                .hasMessageContaining(gameFileId.toString());
        assertThat(fileManager.anyFileDeleteWasAttempted()).isFalse();
    }

    private GameFile mockEnqueuedGameFileExists() {
        GameFile gameFile = TestGameFile.enqueued();
        when(gameFileRepository.getById(gameFile.getId()))
                .thenReturn(gameFile);
        return gameFile;
    }
}