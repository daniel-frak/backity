package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedControllerBeanConfig {

    @Bean
    public PageHttpDtoMapper pageHttpDtoMapper() {
        return Mappers.getMapper(PageHttpDtoMapper.class);
    }

    @Bean
    public PaginationHttpDtoMapper paginationHttpDtoMapper() {
        return Mappers.getMapper(PaginationHttpDtoMapper.class);
    }

    // @TODO Should this be here? Other 'shared' DTOs are elsewhere (e.g. Game, GameFile, FileCopy)
    @Bean
    FileCopyWithContextHttpDtoMapper fileCopyWithContextHttpDtoMapper() {
        return Mappers.getMapper(FileCopyWithContextHttpDtoMapper.class);
    }
}
