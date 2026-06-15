package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryOverviewHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerSliceConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerSliceConfiguration
public class GameContentDiscoveryControllerBeanConfig {

    @Bean
    GameContentDiscoveryOverviewHttpDtoMapper gameContentDiscoveryOverviewHttpDtoMapper() {
        return Mappers.getMapper(GameContentDiscoveryOverviewHttpDtoMapper.class);
    }
}
