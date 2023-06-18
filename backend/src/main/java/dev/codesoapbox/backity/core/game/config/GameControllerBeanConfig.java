package dev.codesoapbox.backity.core.game.config;

import dev.codesoapbox.backity.core.game.adapters.driving.api.http.model.GameWithFilesHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameControllerBeanConfig {

    @Bean
    GameWithFilesHttpDtoMapper gameWithFilesHttpDtoMapper() {
        return Mappers.getMapper(GameWithFilesHttpDtoMapper.class);
    }
}
