package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.FileDiscoveryStatusHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDiscoveryControllerBeanConfig {

    @Bean
    public FileDiscoveryStatusHttpDtoMapper fileDiscoveryStatusHttpDtoMapper() {
        return Mappers.getMapper(FileDiscoveryStatusHttpDtoMapper.class);
    }
}
