package dev.codesoapbox.backity.core.gamefile.config;

import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileControllerBeanConfig {

    @Bean
    public GameFileHttpDtoMapper gameFileHttpDtoMapper() {
        return Mappers.getMapper(GameFileHttpDtoMapper.class);
    }
}
