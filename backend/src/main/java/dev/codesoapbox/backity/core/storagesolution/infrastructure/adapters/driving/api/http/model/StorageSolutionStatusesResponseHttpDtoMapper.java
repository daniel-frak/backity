package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.SharedHttpDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(config = SharedHttpDtoMapperConfig.class,
        uses = {
                StorageSolutionValueObjectHttpDtoMapper.class
        })
public interface StorageSolutionStatusesResponseHttpDtoMapper {

    @Mapping(target = "statuses", source = "statusesById")
    StorageSolutionStatusesResponseHttpDto toDto(Map<StorageSolutionId, StorageSolutionStatus> statusesById);
}
