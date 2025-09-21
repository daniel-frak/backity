package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.*;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTrackerFactory;
import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import dev.codesoapbox.backity.core.backup.application.usecases.RecoverInterruptedFileBackupUseCase;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileBackupUseCaseBeanConfig {

    @Bean
    FileBackupService fileBackupService(UniqueFilePathResolver uniqueFilePathResolver,
                                        FileCopyRepository fileCopyRepository,
                                        List<GameProviderFileBackupService> fileBackupServices,
                                        OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory,
                                        StorageSolutionWriteService storageSolutionWriteService) {
        var fileCopyReplicator = new FileCopyReplicator(
                fileBackupServices, outputStreamProgressTrackerFactory, storageSolutionWriteService);
        return new FileBackupService(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
    }

    @Bean
    OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory(DomainEventPublisher domainEventPublisher) {
        return new OutputStreamProgressTrackerFactory(domainEventPublisher);
    }

    @Bean
    FileCopyReplicationProcess fileCopyReplicationProcess() {
        return new FileCopyReplicationProcess();
    }

    @Bean
    FileBackupContextFactory fileBackupContextFactory(GameFileRepository gameFileRepository,
                                                      BackupTargetRepository backupTargetRepository,
                                                      StorageSolutionRepository storageSolutionRepository) {
        return new FileBackupContextFactory(gameFileRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Bean
    BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase(FileCopyReplicationProcess fileCopyReplicationProcess,
                                                            FileCopyRepository fileCopyRepository,
                                                            FileBackupContextFactory fileBackupContextFactory,
                                                            FileBackupService fileBackupService) {
        return new BackUpOldestFileCopyUseCase(fileCopyReplicationProcess, fileCopyRepository, fileBackupContextFactory,
                fileBackupService);
    }

    @Bean
    RecoverInterruptedFileBackupUseCase recoverInterruptedFileBackupUseCase(
            FileCopyRepository fileCopyRepository, BackupTargetRepository backupTargetRepository,
            StorageSolutionRepository storageSolutionRepository, DomainEventPublisher domainEventPublisher) {
        return new RecoverInterruptedFileBackupUseCase(fileCopyRepository, backupTargetRepository,
                storageSolutionRepository, domainEventPublisher);
    }

    @Bean
    StorageSolutionWriteService storageSolutionWriteService() {
        return new StorageSolutionWriteService();
    }
}
