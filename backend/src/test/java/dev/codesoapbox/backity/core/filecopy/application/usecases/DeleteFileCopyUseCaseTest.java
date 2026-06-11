package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
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

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @BeforeEach
    void setUp() {
        useCase = new DeleteFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Test
    void shouldDeleteFileCopyGivenSourceFileStatusIsStoredIntegrityUnknown() {
        FileCopy fileCopy = storedUnverifiedFileCopyExists();
        FilePath filePath = fileCopy.getFilePath();
        FakeUnixStorageSolution storageSolution = storageSolutionExists(fileCopy);
        storageSolution.createFile(filePath);

        useCase.execute(fileCopy.getId());

        assertThat(storageSolution.fileExists(filePath)).isFalse();
    }

    private FileCopy storedUnverifiedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);
        return fileCopy;
    }

    private FakeUnixStorageSolution storageSolutionExists(FileCopy fileCopy) {
        BackupTarget backupTarget = backupTargetExists(fileCopy);
        return storageSolutionExists(backupTarget);
    }

    private FakeUnixStorageSolution storageSolutionExists(BackupTarget backupTarget) {
        var storageSolution = new FakeUnixStorageSolution();
        lenient().when(storageSolutionRepository.getById(backupTarget.getStorageSolutionId()))
                .thenReturn(storageSolution);
        return storageSolution;
    }

    private BackupTarget backupTargetExists(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        lenient().when(backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId()))
                .thenReturn(backupTarget);
        return backupTarget;
    }

    @Test
    void shouldChangeStatusOfFileCopyGivenDeletingFile() {
        FileCopy fileCopy = storedUnverifiedFileCopyExists();
        storageSolutionExists(fileCopy);

        useCase.execute(fileCopy.getId());

        assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
        verify(fileCopyRepository).save(fileCopy);
    }

    @Test
    void shouldNotChangeStatusOfSourceFileGivenFileDeletionFailed() {
        FileCopy fileCopy = storedUnverifiedFileCopyExists();
        FakeUnixStorageSolution storageSolution = storageSolutionExists(fileCopy);
        var exception = new RuntimeException("test");
        storageSolution.setShouldThrowOnFileDeletion(exception);

        assertThatThrownBy(() -> useCase.execute(fileCopy.getId()))
                .isSameAs(exception);

        assertThat(fileCopy.getStatus()).isNotEqualTo(FileCopyStatus.TRACKED);
        verify(fileCopyRepository, never()).save(any());
    }

    @Test
    void shouldThrowGivenSourceFileStatusIsNotStored() {
        FileCopy fileCopy = enqueuedFileCopyExists();
        FileCopyId fileCopyId = fileCopy.getId();

        assertThatThrownBy(() -> useCase.execute(fileCopyId))
                .isInstanceOf(FileCopyNotBackedUpException.class)
                .hasMessageContaining(fileCopyId.toString());
    }

    private FileCopy enqueuedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);

        return fileCopy;
    }
}