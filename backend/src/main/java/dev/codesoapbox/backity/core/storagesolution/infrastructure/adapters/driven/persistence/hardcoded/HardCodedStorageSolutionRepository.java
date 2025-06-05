package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.hardcoded;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.StorageSolutionNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HardCodedStorageSolutionRepository implements StorageSolutionRepository {

    private final Map<StorageSolutionId, StorageSolution> storageSolutions;

    public HardCodedStorageSolutionRepository(List<StorageSolution> storageSolutions) {
        this.storageSolutions = storageSolutions.stream()
                .collect(Collectors.toMap(StorageSolution::getId, storageSolution -> storageSolution));
    }

    @Override
    public StorageSolution getById(StorageSolutionId id) {
        StorageSolution storageSolution = storageSolutions.get(id);
        if (storageSolution == null) {
            throw new StorageSolutionNotFoundException(id);
        }

        return storageSolution;
    }
}
