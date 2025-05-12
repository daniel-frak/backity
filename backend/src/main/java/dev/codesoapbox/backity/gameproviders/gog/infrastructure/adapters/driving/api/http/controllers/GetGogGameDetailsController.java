package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogGameWithFilesHttpDtoMapper;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogGameDetailsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GogRestResource
@RequiredArgsConstructor
public class GetGogGameDetailsController {

    private final GetGogGameDetailsUseCase useCase;
    private final GogGameWithFilesHttpDtoMapper gameDetailsResponseMapper;

    @Operation(summary = "Get game details", description = "Returns the details of a game")
    @GetMapping("games/{id}")
    public GogGameWithFilesHttpDto getGameDetails(@PathVariable String id) {
        return gameDetailsResponseMapper.toDto(useCase.getGameDetails(id));
    }
}