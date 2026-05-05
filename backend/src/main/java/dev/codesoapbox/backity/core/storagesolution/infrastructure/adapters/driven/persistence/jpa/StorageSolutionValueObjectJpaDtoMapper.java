package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;

public class StorageSolutionValueObjectJpaDtoMapper {

    public String getValue(StorageSolutionId id) {
        return id.value();
    }

    public StorageSolutionId toStorageSolutionId(String value) {
        return new StorageSolutionId(value);
    }

    public FilePath toFilePath(String value) {
        return new FilePath(value);
    }
}
