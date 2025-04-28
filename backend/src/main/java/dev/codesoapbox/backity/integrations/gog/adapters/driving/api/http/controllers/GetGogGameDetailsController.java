package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.GameDetailsResponseHttpDto;
import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.GameDetailsResponseHttpDtoMapper;
import dev.codesoapbox.backity.integrations.gog.application.usecases.GetGogGameDetailsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GogRestResource
@RequiredArgsConstructor
public class GetGogGameDetailsController {

    private final GetGogGameDetailsUseCase useCase;
    private final GameDetailsResponseHttpDtoMapper gameDetailsResponseMapper;

    @Operation(summary = "Get game details", description = "Returns the details of a game")
    @GetMapping("games/{id}")
    public GameDetailsResponseHttpDto getGameDetails(@PathVariable String id) {
        return gameDetailsResponseMapper.toDto(useCase.getGameDetails(id));
    }
}