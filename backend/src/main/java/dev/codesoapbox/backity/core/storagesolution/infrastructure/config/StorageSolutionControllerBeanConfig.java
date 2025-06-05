package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionStatusesResponseHttpDtoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageSolutionControllerBeanConfig {

    @Bean
    StorageSolutionStatusesResponseHttpDtoMapper storageSolutionStatusesResponseHttpDtoMapper() {
        return Mappers.getMapper(StorageSolutionStatusesResponseHttpDtoMapper.class);
    }
}
