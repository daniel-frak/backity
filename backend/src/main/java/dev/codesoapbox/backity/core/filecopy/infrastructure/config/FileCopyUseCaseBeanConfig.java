package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.application.usecases.*;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyUseCaseBeanConfig {

    @Bean
    FileCopyFactory fileCopyFactory() {
        return new FileCopyFactory(FileCopyId::newInstance);
    }

    @Bean
    EnqueueFileCopyUseCase enqueueFileUseCase(FileCopyRepository fileCopyRepositoryRepository,
                                                     FileCopyFactory fileCopyFactory) {
        return new EnqueueFileCopyUseCase(fileCopyRepositoryRepository, fileCopyFactory);
    }

    @Bean
    CancelFileCopyUseCase cancelFileCopyUseCase(FileCopyRepository fileCopyRepository) {
        return new CancelFileCopyUseCase(fileCopyRepository);
    }

    @Bean
    FileCopyWithContextFactory fileCopyWithContextFactory(
            GameFileRepository gameFileRepository, GameRepository gameRepository,
            BackupTargetRepository backupTargetRepository,
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileCopyWithContextFactory(gameFileRepository, gameRepository, backupTargetRepository,
                replicationProgressRepository);
    }

    @Bean
    GetFileCopyQueueUseCase getEnqueuedFileListUseCase(
            FileCopyRepository fileCopyRepository, FileCopyWithContextFactory fileCopyWithContextFactory) {
        return new GetFileCopyQueueUseCase(fileCopyRepository, fileCopyWithContextFactory);
    }

    @Bean
    DeleteFileCopyUseCase deleteFileCopyUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DeleteFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Bean
    DownloadFileCopyUseCase downloadFileUseCase(
            FileCopyRepository fileCopyRepository,
            BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository) {
        return new DownloadFileCopyUseCase(fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }
}
