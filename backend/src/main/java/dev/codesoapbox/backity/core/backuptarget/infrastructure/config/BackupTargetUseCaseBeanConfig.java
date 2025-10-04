package dev.codesoapbox.backity.core.backuptarget.infrastructure.config;

import dev.codesoapbox.backity.core.backuptarget.application.GetBackupTargetsUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class BackupTargetUseCaseBeanConfig {

    @Bean
    GetBackupTargetsUseCase getBackupTargetsUseCase(BackupTargetRepository backupTargetRepository) {
        return new GetBackupTargetsUseCase(backupTargetRepository);
    }
}
