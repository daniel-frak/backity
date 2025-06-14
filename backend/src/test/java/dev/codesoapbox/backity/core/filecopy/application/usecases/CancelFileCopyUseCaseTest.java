package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.application.DownloadService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelFileCopyUseCaseTest {

    private CancelFileCopyUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private DownloadService downloadService;

    @BeforeEach
    void setUp() {
        useCase = new CancelFileCopyUseCase(fileCopyRepository, downloadService);
    }

    @Test
    void shouldCancelEnqueuedFileCopy() {
        FileCopy fileCopy = mockEnqueuedFileCopyExists();

        useCase.cancelFileCopy(fileCopy.getId());

        FileCopy savedFileCopy = getSavedFileCopy();
        assertThat(savedFileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
        verifyNoInteractions(downloadService);
    }

    private FileCopy mockEnqueuedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        return mockFileCopyExists(fileCopy);
    }

    private FileCopy mockFileCopyExists(FileCopy fileCopy) {
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);
        return fileCopy;
    }

    private FileCopy getSavedFileCopy() {
        ArgumentCaptor<FileCopy> fileCopyCaptor = ArgumentCaptor.forClass(FileCopy.class);
        verify(fileCopyRepository).save(fileCopyCaptor.capture());
        return fileCopyCaptor.getValue();
    }

    @Test
    void shouldCancelInProgressFileCopy() {
        FileCopy fileCopy = mockInProgressFileCopyExists();
        String filePath = fileCopy.getFilePath();

        useCase.cancelFileCopy(fileCopy.getId());

        verify(downloadService).cancelDownload(filePath);
        verifyNoMoreInteractions(fileCopyRepository);
    }

    private FileCopy mockInProgressFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        return mockFileCopyExists(fileCopy);
    }
}