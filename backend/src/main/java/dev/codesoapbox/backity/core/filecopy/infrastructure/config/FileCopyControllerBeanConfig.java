package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyControllerBeanConfig {

    @Bean
    public FileCopyHttpDtoMapper fileCopyHttpDtoMapper() {
        return Mappers.getMapper(FileCopyHttpDtoMapper.class);
    }
}
