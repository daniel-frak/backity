package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameWithFilesJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameWithFilesJsonMapper;
import dev.codesoapbox.backity.core.files.application.GameFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Games")
@RestController
@RequestMapping("games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameFacade gameFacade;
    private final GameWithFilesJsonMapper mapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @PageableAsQueryParam
    @GetMapping
    public Page<GameWithFilesJson> getGames(@Parameter(hidden = true) Pageable pageable) {
        return gameFacade.getGamesWithFiles(pageable)
                .map(mapper::toDto);
    }
}
