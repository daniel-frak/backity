package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.RequestPaginationHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class SharedControllerBeanConfig {

    @Bean
    PageHttpDtoMapper pageHttpDtoMapper() {
        return Mappers.getMapper(PageHttpDtoMapper.class);
    }

    @Bean
    RequestPaginationHttpDtoMapper paginationHttpDtoMapper() {
        return Mappers.getMapper(RequestPaginationHttpDtoMapper.class);
    }
}
