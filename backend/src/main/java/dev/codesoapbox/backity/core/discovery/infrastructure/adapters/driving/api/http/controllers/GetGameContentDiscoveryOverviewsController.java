package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryOverviewsUseCase;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryOverviewHttpDto;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model.GameContentDiscoveryOverviewHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@GameContentDiscoveryOverviewsRestResource
@RequiredArgsConstructor
public class GetGameContentDiscoveryOverviewsController {

    private final GetGameContentDiscoveryOverviewsUseCase useCase;
    private final GameContentDiscoveryOverviewHttpDtoMapper gameContentDiscoveryOverviewMapper;

    @Operation(summary = "List game provider content discovery overviews",
            description = "Returns a list of content discovery overviews for every game provider")
    @GetMapping
    public List<GameContentDiscoveryOverviewHttpDto> getDiscoveryOverviews() {
        return useCase.getDiscoveryOverviews().stream()
                .map(gameContentDiscoveryOverviewMapper::toDto)
                .toList();
    }
}
