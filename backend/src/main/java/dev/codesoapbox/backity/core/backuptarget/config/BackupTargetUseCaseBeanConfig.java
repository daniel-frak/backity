package dev.codesoapbox.backity.core.backuptarget.config;

import dev.codesoapbox.backity.core.backuptarget.application.GetBackupTargetsUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackupTargetUseCaseBeanConfig {

    @Bean
    GetBackupTargetsUseCase getBackupTargetsUseCase(BackupTargetRepository backupTargetRepository) {
        return new GetBackupTargetsUseCase(backupTargetRepository);
    }
}
