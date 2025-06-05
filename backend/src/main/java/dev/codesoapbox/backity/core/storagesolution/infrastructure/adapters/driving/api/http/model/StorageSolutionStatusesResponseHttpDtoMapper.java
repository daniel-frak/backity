package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class StorageSolutionStatusesResponseHttpDtoMapper {

    @Mapping(target = "statuses", source = "statusesById")
    public abstract StorageSolutionStatusesResponseHttpDto toDto(
            Map<StorageSolutionId, StorageSolutionStatus> statusesById);

    protected String getValue(StorageSolutionId id) {
        return id.value();
    }
}
