package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model.FileDiscoveryStatusHttpDto;
import dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model.FileDiscoveryStatusHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FileDiscoveryRestResource
@RequiredArgsConstructor
public class GetFileDiscoveryStatusListController {

    private final GetFileDiscoveryStatusListUseCase useCase;
    private final FileDiscoveryStatusHttpDtoMapper fileDiscoveryStatusMapper;

    @Operation(summary = "List discovery statuses",
            description = "Returns a list of discovery statuses for every remote client")
    @GetMapping("statuses")
    public List<FileDiscoveryStatusHttpDto> getStatuses() {
        return useCase.getStatusList().stream()
                .map(fileDiscoveryStatusMapper::toDto)
                .toList();
    }
}
