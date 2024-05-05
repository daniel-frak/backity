package dev.codesoapbox.backity.core.filedetails.config;

import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDetailsControllerBeanConfig {

    @Bean
    public FileDetailsHttpDtoMapper fileDetailsHttpDtoMapper() {
        return Mappers.getMapper(FileDetailsHttpDtoMapper.class);
    }
}
