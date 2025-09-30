package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionStatusesResponseHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerBeanConfiguration
public class StorageSolutionControllerBeanConfig {

    @Bean
    StorageSolutionStatusesResponseHttpDtoMapper storageSolutionStatusesResponseHttpDtoMapper() {
        return Mappers.getMapper(StorageSolutionStatusesResponseHttpDtoMapper.class);
    }
}
