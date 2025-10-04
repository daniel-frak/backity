package dev.codesoapbox.backity.core.gamefile.infrastructure.config;

import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class GameFileControllerBeanConfig {

    @Bean
    GameFileHttpDtoMapper gameFileHttpDtoMapper() {
        return Mappers.getMapper(GameFileHttpDtoMapper.class);
    }
}
