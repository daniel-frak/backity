package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelFileCopyUseCaseTest {

    private CancelFileCopyUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new CancelFileCopyUseCase(fileCopyRepository);
    }

    @Test
    void shouldCancelFileCopy() {
        FileCopy fileCopy = mockEnqueuedFileCopyExists();

        useCase.cancelFileCopy(fileCopy.getId());

        FileCopy savedFileCopy = getSavedFileCopy();
        assertThat(savedFileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
    }

    private FileCopy mockEnqueuedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);
        return fileCopy;
    }

    private FileCopy getSavedFileCopy() {
        ArgumentCaptor<FileCopy> fileCopyCaptor = ArgumentCaptor.forClass(FileCopy.class);
        verify(fileCopyRepository).save(fileCopyCaptor.capture());
        return fileCopyCaptor.getValue();
    }
}