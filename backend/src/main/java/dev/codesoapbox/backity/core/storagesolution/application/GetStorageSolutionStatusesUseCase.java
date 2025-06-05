package dev.codesoapbox.backity.core.storagesolution.application;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class GetStorageSolutionStatusesUseCase {

    private final StorageSolutionRepository storageSolutionRepository;

    public Map<StorageSolutionId, StorageSolutionStatus> getStorageSolutionStatuses() {
        return storageSolutionRepository.findAll().stream()
                .collect(toMap(StorageSolution::getId, StorageSolution::getStatus));
    }
}
