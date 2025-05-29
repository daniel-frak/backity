package dev.codesoapbox.backity.core.backuptarget.config;

import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackupTargetControllerBeanConfig {

    @Bean
    BackupTargetHttpDtoMapper backupTargetHttpDtoMapper() {
        return Mappers.getMapper(BackupTargetHttpDtoMapper.class);
    }
}
