package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.startup.FileBackupRecoveryStartupListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileBackupRecoveryBeanConfig {

    @Bean
    FileBackupRecoveryStartupListener fileBackupRecoveryStartupListener(RecoverInterruptedFileBackupUseCase useCase) {
        return new FileBackupRecoveryStartupListener(useCase);
    }
}
