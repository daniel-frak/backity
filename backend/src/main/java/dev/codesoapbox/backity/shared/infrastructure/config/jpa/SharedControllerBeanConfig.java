package dev.codesoapbox.backity.shared.infrastructure.config.jpa;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDtoMapper;
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
}
