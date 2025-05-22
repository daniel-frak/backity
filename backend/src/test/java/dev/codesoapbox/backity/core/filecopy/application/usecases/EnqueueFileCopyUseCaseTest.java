package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileBackupStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnqueueFileCopyUseCaseTest {

    private EnqueueFileCopyUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new EnqueueFileCopyUseCase(fileCopyRepository);
    }

    @Test
    void shouldSetFileBackupStatusToEnqueuedAndPersistFileCopy() {
        FileCopy fileCopy = mockDiscoveredFileCopyExists();

        useCase.enqueue(fileCopy.getId());

        assertThat(fileCopy.getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
        verify(fileCopyRepository).save(fileCopy);
    }

    private FileCopy mockDiscoveredFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.discovered();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);

        return fileCopy;
    }
}