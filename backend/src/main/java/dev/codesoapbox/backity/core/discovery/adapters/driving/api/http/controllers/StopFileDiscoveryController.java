package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.application.StopFileDiscoveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@FileDiscoveryRestResource
@RequiredArgsConstructor
public class StopFileDiscoveryController {

    private final StopFileDiscoveryUseCase useCase;

    @Operation(summary = "Stop file discovery", description = "Stops the process of file discovery")
    @GetMapping("stop-discovery")
    public void stopDiscovery() {
        useCase.stopFileDiscovery();
    }
}
