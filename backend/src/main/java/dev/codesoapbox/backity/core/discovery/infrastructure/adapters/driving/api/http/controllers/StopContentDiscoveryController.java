package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@GameContentDiscoveryActionsRestResource
@RequiredArgsConstructor
public class StopContentDiscoveryController {

    private final StopGameContentDiscoveryUseCase useCase;

    @Operation(summary = "Stop game content discovery", description = "Stops the process of game content discovery")
    @PostMapping("stop")
    public void stopDiscovery() {
        useCase.stopContentDiscovery();
    }
}
