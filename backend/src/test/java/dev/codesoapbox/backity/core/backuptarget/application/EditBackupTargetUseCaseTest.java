package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
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
class EditBackupTargetUseCaseTest {

    private EditBackupTargetUseCase useCase;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @BeforeEach
    void setUp() {
        useCase = new EditBackupTargetUseCase(backupTargetRepository);
    }

    @Test
    void shouldEditBackupTarget() {
        BackupTarget initialBackupTarget = TestBackupTarget.localFolderBuilder()
                .withName("Original name")
                .build();
        exists(initialBackupTarget);
        BackupTarget expectedBackupTarget = TestBackupTarget.localFolderBuilder()
                .withName("New name")
                .build();
        EditBackupTargetCommand command = toCommand(expectedBackupTarget);

        useCase.editBackupTarget(command);

        assertWasPersisted(expectedBackupTarget);
    }

    private void exists(BackupTarget initialBackupTarget) {
        when(backupTargetRepository.getById(initialBackupTarget.getId()))
                .thenReturn(initialBackupTarget);
    }

    private EditBackupTargetCommand toCommand(BackupTarget backupTarget) {
        return new EditBackupTargetCommand(
                backupTarget.getId(),
                backupTarget.getName()
        );
    }

    private void assertWasPersisted(BackupTarget expectedBackupTarget) {
        ArgumentCaptor<BackupTarget> savedBackupTargetCaptor = ArgumentCaptor.forClass(BackupTarget.class);
        verify(backupTargetRepository).save(savedBackupTargetCaptor.capture());
        assertThat(savedBackupTargetCaptor.getValue()).usingRecursiveComparison()
                .isEqualTo(expectedBackupTarget);
    }
}