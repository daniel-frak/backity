package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBackupTargetsUseCaseTest {

    private GetBackupTargetsUseCase useCase;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetBackupTargetsUseCase(backupTargetRepository);
    }

    @Test
    void shouldGetBackupTargets() {
        List<BackupTarget> backupTargets = mockBackupTargetsExist();

        List<BackupTarget> result = useCase.getBackupTargets();

        assertThat(result).isEqualTo(backupTargets);
    }

    private List<BackupTarget> mockBackupTargetsExist() {
        List<BackupTarget> backupTargets = List.of(TestBackupTarget.localFolder());
        when(backupTargetRepository.findAllBackupTargets())
                .thenReturn(backupTargets);
        return backupTargets;
    }
}