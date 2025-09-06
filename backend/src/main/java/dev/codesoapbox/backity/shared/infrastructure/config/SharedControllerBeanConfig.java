package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.RequestPaginationHttpDtoMapper;
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
    public RequestPaginationHttpDtoMapper paginationHttpDtoMapper() {
        return Mappers.getMapper(RequestPaginationHttpDtoMapper.class);
    }
}
