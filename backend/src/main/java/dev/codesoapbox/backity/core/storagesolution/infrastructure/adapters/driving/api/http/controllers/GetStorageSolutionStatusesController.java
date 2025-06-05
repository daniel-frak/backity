package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.storagesolution.application.GetStorageSolutionStatusesUseCase;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionStatusesResponseHttpDto;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model.StorageSolutionStatusesResponseHttpDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RequiredArgsConstructor
@StorageSolutionStatusesRestResource
public class GetStorageSolutionStatusesController {

    private final GetStorageSolutionStatusesUseCase getStorageSolutionStatusesUseCase;
    private final StorageSolutionStatusesResponseHttpDtoMapper mapper;

    @GetMapping
    public StorageSolutionStatusesResponseHttpDto getStorageSolutionStatuses() {
        Map<StorageSolutionId, StorageSolutionStatus> storageSolutionStatuses =
                getStorageSolutionStatusesUseCase.getStorageSolutionStatuses();

        return mapper.toDto(storageSolutionStatuses);
    }
}
