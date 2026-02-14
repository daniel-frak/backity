package dev.codesoapbox.backity.core.backuptarget.infrastructure.config;

import dev.codesoapbox.backity.core.backuptarget.application.*;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class BackupTargetUseCaseBeanConfig {

    @Bean
    GetBackupTargetsUseCase getBackupTargetsUseCase(BackupTargetRepository backupTargetRepository) {
        return new GetBackupTargetsUseCase(backupTargetRepository);
    }

    @Bean
    GetLockedBackupTargetIdsUseCase getLockedBackupTargetIdsUseCase(FileCopyRepository fileCopyRepository) {
        return new GetLockedBackupTargetIdsUseCase(fileCopyRepository);
    }

    @Bean
    AddBackupTargetUseCase addBackupTargetUseCase(BackupTargetRepository backupTargetRepository) {
        return new AddBackupTargetUseCase(backupTargetRepository);
    }

    @Bean
    EditBackupTargetUseCase editBackupTargetUseCase(BackupTargetRepository backupTargetRepository) {
        return new EditBackupTargetUseCase(backupTargetRepository);
    }

    @Bean
    DeleteBackupTargetUseCase deleteBackupTargetUseCase(BackupTargetRepository backupTargetRepository,
                                                        FileCopyRepository fileCopyRepository) {
        return new DeleteBackupTargetUseCase(backupTargetRepository, fileCopyRepository);
    }
}
