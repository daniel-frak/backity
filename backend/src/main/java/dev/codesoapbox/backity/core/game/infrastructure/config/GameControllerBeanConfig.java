package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameControllerBeanConfig {

    @Bean
    GameWithFileCopiesHttpDtoMapper gameWithFilesHttpDtoMapper() {
        return Mappers.getMapper(GameWithFileCopiesHttpDtoMapper.class);
    }
}
