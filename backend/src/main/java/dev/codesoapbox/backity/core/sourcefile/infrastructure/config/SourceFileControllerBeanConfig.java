package dev.codesoapbox.backity.core.sourcefile.infrastructure.config;

import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile.SourceFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerSliceConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerSliceConfiguration
public class SourceFileControllerBeanConfig {

    @Bean
    SourceFileHttpDtoMapper sourceFileHttpDtoMapper() {
        return Mappers.getMapper(SourceFileHttpDtoMapper.class);
    }
}
