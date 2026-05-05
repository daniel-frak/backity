package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;

public class StorageSolutionValueObjectHttpDtoMapper {

    public String getValue(StorageSolutionId id) {
        return id.value();
    }
}
