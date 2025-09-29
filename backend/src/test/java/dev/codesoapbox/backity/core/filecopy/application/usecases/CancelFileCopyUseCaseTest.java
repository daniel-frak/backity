package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.application.StorageSolutionWriteService;
import dev.codesoapbox.backity.core.backup.application.WriteDestination;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
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
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionWriteService storageSolutionWriteService;

    @BeforeEach
    void setUp() {
        useCase = new CancelFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionWriteService);
    }

    @Test
    void shouldDoNothingGivenFileCopyNotEnqueuedAndNotInProgress() {
        FileCopy fileCopy = mockTrackedFileCopyExists();

        useCase.cancelFileCopy(fileCopy.getId());
        verifyNoMoreInteractions(fileCopyRepository, storageSolutionWriteService);
    }

    private FileCopy mockTrackedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.tracked();
        return mockFileCopyExists(fileCopy);
    }

    @Test
    void shouldCancelEnqueuedFileCopy() {
        FileCopy fileCopy = mockEnqueuedFileCopyExists();

        useCase.cancelFileCopy(fileCopy.getId());

        FileCopy savedFileCopy = getSavedFileCopy();
        assertThat(savedFileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
        verifyNoInteractions(storageSolutionWriteService);
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
        BackupTarget backupTarget = mockBackupTargetExistsFor(fileCopy);
        String filePath = fileCopy.getFilePath();

        useCase.cancelFileCopy(fileCopy.getId());

        var expectedWriteDestination = new WriteDestination(backupTarget.getStorageSolutionId(), filePath);
        verify(storageSolutionWriteService).cancelWrite(expectedWriteDestination);
        verifyNoMoreInteractions(fileCopyRepository);
    }

    private BackupTarget mockBackupTargetExistsFor(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        when(backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId()))
                .thenReturn(backupTarget);
        return backupTarget;
    }

    private FileCopy mockInProgressFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        return mockFileCopyExists(fileCopy);
    }
}