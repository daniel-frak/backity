package dev.codesoapbox.backity.core.files.config.gamefiledetails;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileDetailsControllerBeanConfig {

    @Bean
    public GameFileDetailsHttpDtoMapper gameFileDetailsHttpDtoMapper() {
        return Mappers.getMapper(GameFileDetailsHttpDtoMapper.class);
    }
}
