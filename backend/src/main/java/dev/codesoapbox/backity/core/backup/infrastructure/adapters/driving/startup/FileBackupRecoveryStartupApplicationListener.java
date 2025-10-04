package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@RequiredArgsConstructor
public class FileBackupRecoveryStartupApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final RecoverInterruptedFileBackupUseCase useCase;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        useCase.recoverInterruptedFileBackup();
    }
}
