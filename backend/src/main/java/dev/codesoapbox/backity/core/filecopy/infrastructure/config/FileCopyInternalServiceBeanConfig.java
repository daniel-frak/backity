package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceBeanConfiguration;
import org.springframework.context.annotation.Bean;

@InternalApplicationServiceBeanConfiguration
public class FileCopyInternalServiceBeanConfig {

    @Bean
    FileCopyFactory fileCopyFactory() {
        return new FileCopyFactory(FileCopyId::newInstance);
    }

    @Bean
    FileCopyWithContextFactory fileCopyWithContextFactory(
            GameFileRepository gameFileRepository, GameRepository gameRepository,
            BackupTargetRepository backupTargetRepository,
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new FileCopyWithContextFactory(gameFileRepository, gameRepository, backupTargetRepository,
                replicationProgressRepository);
    }
}
