package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
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
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        useCase = new DeleteFileCopyUseCase(storageSolution, fileCopyRepository);
    }

    @Test
    void shouldDeleteFileCopyGivenGameFileStatusIsSuccess() {
        FileCopy fileCopy = mockSuccessfulFileCopyExists();
        String filePath = fileCopy.getFilePath();

        useCase.deleteFileCopy(fileCopy.getId());

        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
    }

    private FileCopy mockSuccessfulFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.successful();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);
        return fileCopy;
    }

    @Test
    void shouldChangeStatusOfFileCopyWhenDeletingFile() {
        FileCopy fileCopy = mockSuccessfulFileCopyExists();

        useCase.deleteFileCopy(fileCopy.getId());

        assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.DISCOVERED);
        verify(fileCopyRepository).save(fileCopy);
    }

    @Test
    void shouldNotChangeStatusOfGameFileGivenFileDeletionFailed() {
        var exception = new RuntimeException("test");
        storageSolution.setShouldThrowOnFileDeletion(exception);
        FileCopy fileCopy = mockSuccessfulFileCopyExists();

        assertThatThrownBy(() -> useCase.deleteFileCopy(fileCopy.getId()))
                .isSameAs(exception);

        assertThat(fileCopy.getStatus()).isNotEqualTo(FileCopyStatus.DISCOVERED);
        verify(fileCopyRepository, never()).save(any());
    }

    @Test
    void shouldThrowGivenGameFileStatusIsNotSuccess() {
        FileCopy fileCopy = mockEnqueuedFileCopyExists();
        FileCopyId fileCopyId = fileCopy.getId();

        assertThatThrownBy(() -> useCase.deleteFileCopy(fileCopyId))
                .isInstanceOf(FileCopyNotBackedUpException.class)
                .hasMessageContaining(fileCopyId.toString());
        assertThat(storageSolution.anyFileDeleteWasAttempted()).isFalse();
    }

    private FileCopy mockEnqueuedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);

        return fileCopy;
    }
}