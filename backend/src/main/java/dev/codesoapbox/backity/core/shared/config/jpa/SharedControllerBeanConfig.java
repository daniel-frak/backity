package dev.codesoapbox.backity.core.shared.config.jpa;

import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJsonMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationJsonMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedControllerBeanConfig {

    @Bean
    public PageJsonMapper pageJsonMapper() {
        return Mappers.getMapper(PageJsonMapper.class);
    }

    @Bean
    public PaginationJsonMapper paginationJsonMapper() {
        return Mappers.getMapper(PaginationJsonMapper.class);
    }
}
