package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoverInterruptedFileBackupUseCaseTest {

    private RecoverInterruptedFileBackupUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        useCase = new RecoverInterruptedFileBackupUseCase(
                fileCopyRepository, backupTargetRepository, storageSolutionRepository, domainEventPublisher);
    }

    @Test
    void shouldRecoverInterruptedFileBackup() {
        FileCopy fileCopy = mockInProgressFileCopyExists();
        FakeUnixStorageSolution storageSolution = mockStorageSolutionExists();
        mockBackupTargetExists(fileCopy, storageSolution);
        String filePathToDelete = fileCopy.getFilePath();

        useCase.recoverInterruptedFileBackup();

        assertThat(storageSolution.fileDeleteWasAttempted(filePathToDelete)).isTrue();
        FileCopy savedFileCopy = getSavedFileCopy();
        assertThat(savedFileCopy.getId()).isEqualTo(fileCopy.getId());
        assertThat(savedFileCopy.getStatus()).isEqualTo(FileCopyStatus.FAILED);
        assertThat(savedFileCopy.getFailedReason()).isEqualTo("Backup was interrupted before completion");
        assertThat(savedFileCopy.getFilePath()).isNull();
    }

    private FileCopy mockInProgressFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        when(fileCopyRepository.findAllInProgress())
                .thenReturn(List.of(fileCopy));
        return fileCopy;
    }

    private void mockBackupTargetExists(FileCopy fileCopy, StorageSolution storageSolution) {
        BackupTarget backupTarget = TestBackupTarget.localFolderBuilder()
                .withId(fileCopy.getNaturalId().backupTargetId())
                .withStorageSolutionId(storageSolution.getId())
                .build();
        when(backupTargetRepository.findAllByIdIn(Set.of(fileCopy.getNaturalId().backupTargetId())))
                .thenReturn(List.of(backupTarget));
    }

    private FakeUnixStorageSolution mockStorageSolutionExists() {
        var storageSolution = new FakeUnixStorageSolution();
        when(storageSolutionRepository.findAll())
                .thenReturn(List.of(storageSolution));

        return storageSolution;
    }

    private FileCopy getSavedFileCopy() {
        ArgumentCaptor<FileCopy> savedFileCopyCaptor = ArgumentCaptor.forClass(FileCopy.class);
        verify(fileCopyRepository).save(savedFileCopyCaptor.capture());
        return savedFileCopyCaptor.getValue();
    }

    @Test
    void shouldPublishEventOnCompletion() {
        FileCopy fileCopy = mockInProgressFileCopyExists();
        FakeUnixStorageSolution storageSolution = mockStorageSolutionExists();
        mockBackupTargetExists(fileCopy, storageSolution);

        useCase.recoverInterruptedFileBackup();

        verify(domainEventPublisher).publish(new BackupRecoveryCompletedEvent());
    }
}