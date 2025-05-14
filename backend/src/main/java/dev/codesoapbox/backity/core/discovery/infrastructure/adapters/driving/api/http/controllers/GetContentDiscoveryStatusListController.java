package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryStatusHttpDto;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryStatusHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@GameContentDiscoveryStatusRestResource
@RequiredArgsConstructor
public class GetContentDiscoveryStatusListController {

  private final GetGameContentDiscoveryStatusListUseCase useCase;
    private final GameContentDiscoveryStatusHttpDtoMapper gameContentDiscoveryStatusMapper;

    @Operation(summary = "List game provider content discovery statuses",
            description = "Returns a list of content discovery statuses for every game provider")
    @GetMapping
    public List<GameContentDiscoveryStatusHttpDto> getStatuses() {
        return useCase.getStatusList().stream()
                .map(gameContentDiscoveryStatusMapper::toDto)
                .toList();
    }
}
