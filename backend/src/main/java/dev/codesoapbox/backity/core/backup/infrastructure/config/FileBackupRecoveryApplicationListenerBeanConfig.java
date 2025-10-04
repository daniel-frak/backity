package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup.FileBackupRecoveryStartupApplicationListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringApplicationListenerBeanConfiguration
public class FileBackupRecoveryApplicationListenerBeanConfig {

    @Bean
    FileBackupRecoveryStartupApplicationListener fileBackupRecoveryStartupApplicationListener(
            RecoverInterruptedFileBackupUseCase useCase) {
        return new FileBackupRecoveryStartupApplicationListener(useCase);
    }
}
