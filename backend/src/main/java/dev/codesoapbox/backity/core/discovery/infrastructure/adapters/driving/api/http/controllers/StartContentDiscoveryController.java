package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.StartGameContentDiscoveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@GameContentDiscoveryActionsRestResource
@RequiredArgsConstructor
public class StartContentDiscoveryController {

    private final StartGameContentDiscoveryUseCase useCase;

    @Operation(summary = "Start game content discovery", description = "Starts the process of game content discovery")
    @PostMapping("start")
    public void startDiscovery() {
        useCase.startContentDiscovery();
    }
}
