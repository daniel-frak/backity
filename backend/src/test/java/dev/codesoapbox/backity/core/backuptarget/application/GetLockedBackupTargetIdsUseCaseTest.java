package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLockedBackupTargetIdsUseCaseTest {

    private GetLockedBackupTargetIdsUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetLockedBackupTargetIdsUseCase(fileCopyRepository);
    }

    @Test
    void shouldGetLockedBackupTargetIds() {
        BackupTargetId backupTargetId = TestBackupTarget.localFolder().getId();
        backupTargetIsUsed(backupTargetId);

        List<BackupTargetId> result = useCase.getLockedBackupTargetIds();

        assertThat(result).isEqualTo(List.of(backupTargetId));
    }

    private void backupTargetIsUsed(BackupTargetId backupTargetId) {
        when(fileCopyRepository.getUniqueBackupTargetIdsByStatusNotIn(
                FileCopyStatus.NON_LOCKING_STATUSES))
                .thenReturn(List.of(backupTargetId));
    }
}