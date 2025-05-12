package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogConfigHttpDtoMapper;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogControllerBeanConfig {

    @Bean
    public GogGameWithFilesHttpDtoMapper gameDetailsResponseHttpDtoMapper() {
        return Mappers.getMapper(GogGameWithFilesHttpDtoMapper.class);
    }

    @Bean
    public GogConfigHttpDtoMapper gogConfigResponseHttpDtoMapper() {
        return Mappers.getMapper(GogConfigHttpDtoMapper.class);
    }
}
