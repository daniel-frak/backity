package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesReadModelHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerSliceConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerSliceConfiguration
public class GameControllerBeanConfig {

    @Bean
    GameWithFileCopiesReadModelHttpDtoMapper gameWithFilesHttpDtoMapper() {
        return Mappers.getMapper(GameWithFileCopiesReadModelHttpDtoMapper.class);
    }
}
