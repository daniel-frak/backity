package dev.codesoapbox.backity.core.files.config.game;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameWithFilesJsonMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameControllerBeanConfig {

    @Bean
    GameWithFilesJsonMapper gameWithFilesJsonMapper() {
        return Mappers.getMapper(GameWithFilesJsonMapper.class);
    }
}
