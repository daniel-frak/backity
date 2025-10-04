package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.*;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTrackerFactory;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceBeanConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@InternalApplicationServiceBeanConfiguration
public class FileBackupInternalServiceBeanConfig {

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
    StorageSolutionWriteService storageSolutionWriteService() {
        return new StorageSolutionWriteService();
    }
}
