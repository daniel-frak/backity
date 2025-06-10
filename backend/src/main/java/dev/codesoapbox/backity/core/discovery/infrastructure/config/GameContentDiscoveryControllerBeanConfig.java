package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryOverviewHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryControllerBeanConfig {

    @Bean
    public GameContentDiscoveryOverviewHttpDtoMapper gameContentDiscoveryOverviewHttpDtoMapper() {
        return Mappers.getMapper(GameContentDiscoveryOverviewHttpDtoMapper.class);
    }
}
