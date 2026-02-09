package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GogRestResource
@RequiredArgsConstructor
public class GetGogGameWithFilesController {

    private final GetGogGameDetailsUseCase useCase;
    private final GogGameWithFilesHttpDtoMapper gameDetailsResponseMapper;

    @Operation(summary = "Get GOG game details", description = "Returns the details of a game")
    @GetMapping("games/{id}")
    public ResponseEntity<GogGameWithFilesHttpDto> getGogGameWithFiles(@PathVariable String id) {
        return useCase.getGameDetails(id)
                .map(gameDetailsResponseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}