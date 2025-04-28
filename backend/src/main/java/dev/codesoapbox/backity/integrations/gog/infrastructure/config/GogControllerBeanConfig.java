package dev.codesoapbox.backity.integrations.gog.infrastructure.config;

import dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model.GameDetailsResponseHttpDtoMapper;
import dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model.GogConfigResponseHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogControllerBeanConfig {

    @Bean
    public GameDetailsResponseHttpDtoMapper gameDetailsResponseHttpDtoMapper() {
        return Mappers.getMapper(GameDetailsResponseHttpDtoMapper.class);
    }

    @Bean
    public GogConfigResponseHttpDtoMapper gogConfigResponseHttpDtoMapper() {
        return Mappers.getMapper(GogConfigResponseHttpDtoMapper.class);
    }
}
