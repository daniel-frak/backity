package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@SpringBootTest(classes = FileBackupRecoveryStartupApplicationListener.class)
class FileBackupRecoveryStartupApplicationListenerIT {

    @MockitoBean
    private RecoverInterruptedFileBackupUseCase useCase;

    @Test
    void shouldTriggerRecoveryOnEvent() {
        verify(useCase).recoverInterruptedFileBackup();
    }
}