package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceSliceConfiguration;
import org.springframework.context.annotation.Bean;

@InternalApplicationServiceSliceConfiguration
public class FileCopyInternalServiceBeanConfig {

    @Bean
    FileCopyFactory fileCopyFactory() {
        return new FileCopyFactory(FileCopyId::newInstance);
    }

    @Bean
    FileCopyWithContextFactory fileCopyWithContextFactory(
            SourceFileRepository sourceFileRepository, GameRepository gameRepository,
            BackupTargetRepository backupTargetRepository,
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileCopyWithContextFactory(sourceFileRepository, gameRepository, backupTargetRepository,
                replicationProgressRepository);
    }
}
