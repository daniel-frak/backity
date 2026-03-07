package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetIsInUseException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteBackupTargetUseCaseTest {

    private DeleteBackupTargetUseCase useCase;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new DeleteBackupTargetUseCase(backupTargetRepository, fileCopyRepository);
    }

    @Test
    void shouldDeleteBackupTargetGivenNoFileCopiesAreUsingIt() {
        BackupTarget backupTarget = TestBackupTarget.localFolder();

        useCase.deleteBackupTarget(backupTarget.getId());

        verify(backupTargetRepository).deleteById(backupTarget.getId());
    }

    @Test
    void shouldThrowGivenFileCopiesAreUsingBackupTarget() {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        fileCopiesExistFor(backupTarget);
        BackupTargetId backupTargetId = backupTarget.getId();

        assertThatThrownBy(() -> useCase.deleteBackupTarget(backupTargetId))
                .isInstanceOf(BackupTargetIsInUseException.class)
                .hasMessageContaining(backupTargetId.value().toString());
    }

    private void fileCopiesExistFor(BackupTarget backupTarget) {
        when(fileCopyRepository.existByBackupTargetId(backupTarget.getId()))
                .thenReturn(true);
    }
}