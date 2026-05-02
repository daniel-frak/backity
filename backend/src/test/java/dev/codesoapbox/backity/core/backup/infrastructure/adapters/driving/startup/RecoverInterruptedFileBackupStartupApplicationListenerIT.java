package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringApplicationListenerTest;
import dev.codesoapbox.backity.testing.messaging.application.ApplicationEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@SpringApplicationListenerTest
class RecoverInterruptedFileBackupStartupApplicationListenerIT {

    @Autowired
    private RecoverInterruptedFileBackupUseCase useCase;

    @Test
    void shouldTriggerOnApplicationStartup(ApplicationEventScenario scenario) {
        scenario.verifyAfterStartup(() -> verify(useCase).recoverInterruptedFileBackup());
    }
}