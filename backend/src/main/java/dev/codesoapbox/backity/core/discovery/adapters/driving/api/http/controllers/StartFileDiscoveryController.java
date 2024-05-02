package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.application.StartFileDiscoveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@FileDiscoveryRestResource
@RequiredArgsConstructor
public class StartFileDiscoveryController {

    private final StartFileDiscoveryUseCase useCase;

    @Operation(summary = "Start file discovery", description = "Starts the process of file discovery")
    @GetMapping("discover")
    public void discover() {
        useCase.startFileDiscovery();
    }
}
