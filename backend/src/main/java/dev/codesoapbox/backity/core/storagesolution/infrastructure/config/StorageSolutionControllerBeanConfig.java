package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionStatusesResponseHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerSliceConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@ControllerSliceConfiguration
public class StorageSolutionControllerBeanConfig {

    @Bean
    StorageSolutionStatusesResponseHttpDtoMapper storageSolutionStatusesResponseHttpDtoMapper() {
        return Mappers.getMapper(StorageSolutionStatusesResponseHttpDtoMapper.class);
    }
}
