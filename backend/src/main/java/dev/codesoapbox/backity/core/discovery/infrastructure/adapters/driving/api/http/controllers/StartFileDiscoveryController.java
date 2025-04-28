package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.StartFileDiscoveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@FileDiscoveryActionsRestResource
@RequiredArgsConstructor
public class StartFileDiscoveryController {

    private final StartFileDiscoveryUseCase useCase;

    @Operation(summary = "Start file discovery", description = "Starts the process of file discovery")
    @PostMapping("start")
    public void startDiscovery() {
        useCase.startFileDiscovery();
    }
}
