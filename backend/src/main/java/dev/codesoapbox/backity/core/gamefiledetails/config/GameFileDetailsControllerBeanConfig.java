package dev.codesoapbox.backity.core.gamefiledetails.config;

import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDtoMapper;
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
