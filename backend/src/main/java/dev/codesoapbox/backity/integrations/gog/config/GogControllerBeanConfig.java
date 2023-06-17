package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.GameDetailsResponseHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogControllerBeanConfig {

    @Bean
    public GameDetailsResponseHttpDtoMapper gameDetailsResponseHttpDtoMapper() {
        return Mappers.getMapper(GameDetailsResponseHttpDtoMapper.class);
    }
}
