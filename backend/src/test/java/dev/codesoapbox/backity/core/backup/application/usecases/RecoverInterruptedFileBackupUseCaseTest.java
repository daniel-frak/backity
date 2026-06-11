package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
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
        FileCopy fileCopy = TestFileCopy.inProgress();
        FakeUnixStorageSolution storageSolution = aStorageSolution();
        storageSolution.createFile(fileCopy.getFilePath());
        existsInProgress(fileCopy);
        exists(storageSolution);
        aBackupTargetExists(fileCopy, storageSolution);
        FilePath filePathToDelete = fileCopy.getFilePath();

        useCase.execute();

        assertThat(storageSolution.fileExists(filePathToDelete)).isFalse();
        FileCopy savedFileCopy = getSavedFileCopy();
        assertThat(savedFileCopy.getId()).isEqualTo(fileCopy.getId());
        assertThat(savedFileCopy.getStatus()).isEqualTo(FileCopyStatus.FAILED);
        assertThat(savedFileCopy.getFailedReason())
                .isEqualTo(new FileCopyFailureReason("Backup was interrupted before completion"));
        assertThat(savedFileCopy.getFilePath()).isNull();
    }

    private void existsInProgress(FileCopy fileCopy) {
        when(fileCopyRepository.findAllInProgress())
                .thenReturn(List.of(fileCopy));
    }

    private void aBackupTargetExists(FileCopy fileCopy, StorageSolution storageSolution) {
        BackupTarget backupTarget = TestBackupTarget.localFolderBuilder()
                .withId(fileCopy.getNaturalId().backupTargetId())
                .withStorageSolutionId(storageSolution.getId())
                .build();
        when(backupTargetRepository.findAllByIdIn(Set.of(fileCopy.getNaturalId().backupTargetId())))
                .thenReturn(List.of(backupTarget));
    }

    private FakeUnixStorageSolution aStorageSolution() {
        return new FakeUnixStorageSolution();
    }

    private void exists(FakeUnixStorageSolution storageSolution) {
        when(storageSolutionRepository.findAll())
                .thenReturn(List.of(storageSolution));
    }

    private FileCopy getSavedFileCopy() {
        ArgumentCaptor<FileCopy> savedFileCopyCaptor = ArgumentCaptor.forClass(FileCopy.class);
        verify(fileCopyRepository).save(savedFileCopyCaptor.capture());
        return savedFileCopyCaptor.getValue();
    }

    @Test
    void shouldPublishEventOnCompletion() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        existsInProgress(fileCopy);
        FakeUnixStorageSolution storageSolution = aStorageSolution();
        exists(storageSolution);
        aBackupTargetExists(fileCopy, storageSolution);

        useCase.execute();

        verify(domainEventPublisher).publish(new BackupRecoveryCompletedEvent());
    }
}