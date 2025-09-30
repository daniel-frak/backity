package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryOverviewHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class GameContentDiscoveryControllerBeanConfig {

    @Bean
    public GameContentDiscoveryOverviewHttpDtoMapper gameContentDiscoveryOverviewHttpDtoMapper() {
        return Mappers.getMapper(GameContentDiscoveryOverviewHttpDtoMapper.class);
    }
}
