package dev.codesoapbox.backity.core.backuptarget.infrastructure.config;

import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class BackupTargetControllerBeanConfig {

    @Bean
    BackupTargetHttpDtoMapper backupTargetHttpDtoMapper() {
        return Mappers.getMapper(BackupTargetHttpDtoMapper.class);
    }
}
