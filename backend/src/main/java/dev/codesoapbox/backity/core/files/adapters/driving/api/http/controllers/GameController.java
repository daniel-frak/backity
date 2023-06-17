package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameWithFilesJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameWithFilesJsonMapper;
import dev.codesoapbox.backity.core.files.application.GameFacade;
import dev.codesoapbox.backity.core.files.application.GameWithFiles;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJson;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJsonMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationJson;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationJsonMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PaginationJsonMapper paginationMapper;
    private final PageJsonMapper pageJsonMapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @GetMapping
    public PageJson<GameWithFilesJson> getGames(PaginationJson pagination) {
        Page<GameWithFiles> gamesWithFiles = gameFacade.getGamesWithFiles(paginationMapper.toModel(pagination));
        return pageJsonMapper.toJson(gamesWithFiles, mapper::toDto);
    }
}
