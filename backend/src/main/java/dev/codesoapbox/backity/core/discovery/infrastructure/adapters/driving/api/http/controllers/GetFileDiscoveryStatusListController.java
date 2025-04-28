package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.FileDiscoveryStatusHttpDto;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.FileDiscoveryStatusHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FileDiscoveryStatusRestResource
@RequiredArgsConstructor
public class GetFileDiscoveryStatusListController {

  private final GetFileDiscoveryStatusListUseCase useCase;
    private final FileDiscoveryStatusHttpDtoMapper fileDiscoveryStatusMapper;

    @Operation(summary = "List game provider file discovery statuses",
            description = "Returns a list of file discovery statuses for every game provider")
    @GetMapping
    public List<FileDiscoveryStatusHttpDto> getStatuses() {
        return useCase.getStatusList().stream()
                .map(fileDiscoveryStatusMapper::toDto)
                .toList();
    }
}
