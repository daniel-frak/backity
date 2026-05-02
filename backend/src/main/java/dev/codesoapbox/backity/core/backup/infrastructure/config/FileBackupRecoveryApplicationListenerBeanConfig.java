package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup.RecoverInterruptedFileBackupStartupApplicationListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringApplicationListenerBeanConfiguration
public class FileBackupRecoveryApplicationListenerBeanConfig {

    @Bean
    RecoverInterruptedFileBackupStartupApplicationListener fileBackupRecoveryStartupApplicationListener(
            RecoverInterruptedFileBackupUseCase useCase) {
        return new RecoverInterruptedFileBackupStartupApplicationListener(useCase);
    }
}
