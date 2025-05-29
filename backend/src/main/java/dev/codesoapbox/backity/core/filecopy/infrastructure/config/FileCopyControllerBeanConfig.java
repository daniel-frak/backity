package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyControllerBeanConfig {

    @Bean
    public FileCopyHttpDtoMapper fileCopyHttpDtoMapper() {
        return Mappers.getMapper(FileCopyHttpDtoMapper.class);
    }

    @Bean
    FileCopyWithContextHttpDtoMapper fileCopyWithContextHttpDtoMapper() {
        return Mappers.getMapper(FileCopyWithContextHttpDtoMapper.class);
    }
}
