package dev.codesoapbox.backity.core.backuptarget.infrastructure.config;

import dev.codesoapbox.backity.core.backuptarget.adapters.driven.persistence.hardcoded.HardCodedBackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackupTargetHardCodedRepositoryBeanConfig {

    @Bean
    BackupTargetRepository backupTargetRepository(
            @Value("${backity.filesystem.s3.enabled}") boolean s3Enabled,
            @Value("${backity.filesystem.local.enabled}") boolean localEnabled,
            @Value("${backity.default-path-template}") String defaultPathTemplate
    ) {
        return new HardCodedBackupTargetRepository(s3Enabled, localEnabled, defaultPathTemplate);
    }
}
