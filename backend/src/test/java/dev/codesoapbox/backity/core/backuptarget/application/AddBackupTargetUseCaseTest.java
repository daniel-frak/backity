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

@ExtendWith(MockitoExtension.class)
class AddBackupTargetUseCaseTest {

    private AddBackupTargetUseCase useCase;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @BeforeEach
    void setUp() {
        useCase = new AddBackupTargetUseCase(backupTargetRepository);
    }

    @Test
    void shouldAddBackupTarget() {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        AddBackupTargetCommand command = toCommand(backupTarget);

        BackupTarget result = useCase.addBackupTarget(command);

        assertSame(result, backupTarget);
        assertWasPersisted(result);
    }

    private void assertSame(BackupTarget result, BackupTarget backupTarget) {
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "dateCreated", "dateModified")
                .isEqualTo(backupTarget);
    }

    private void assertWasPersisted(BackupTarget result) {
        ArgumentCaptor<BackupTarget> savedBackupTargetCaptor = ArgumentCaptor.forClass(BackupTarget.class);
        verify(backupTargetRepository).save(savedBackupTargetCaptor.capture());
        assertThat(savedBackupTargetCaptor.getValue()).usingRecursiveComparison()
                .isEqualTo(result);
    }

    private AddBackupTargetCommand toCommand(BackupTarget backupTarget) {
        return new AddBackupTargetCommand(
                backupTarget.getName(),
                backupTarget.getStorageSolutionId(),
                backupTarget.getPathTemplate()
        );
    }
}