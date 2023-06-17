package dev.codesoapbox.backity.core.files.config.gamefiledetails;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsJsonMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileDetailsJsonBeanConfig {

    @Bean
    public GameFileDetailsJsonMapper gameFileDetailsJsonMapper() {
        return Mappers.getMapper(GameFileDetailsJsonMapper.class);
    }
}
