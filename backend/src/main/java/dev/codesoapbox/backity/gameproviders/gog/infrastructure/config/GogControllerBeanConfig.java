package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogConfigHttpDtoMapper;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class GogControllerBeanConfig {

    @Bean
    GogGameWithFilesHttpDtoMapper gameDetailsResponseHttpDtoMapper() {
        return Mappers.getMapper(GogGameWithFilesHttpDtoMapper.class);
    }

    @Bean
    GogConfigHttpDtoMapper gogConfigResponseHttpDtoMapper() {
        return Mappers.getMapper(GogConfigHttpDtoMapper.class);
    }
}
