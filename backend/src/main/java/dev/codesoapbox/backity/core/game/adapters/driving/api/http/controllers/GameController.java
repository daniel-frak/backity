package dev.codesoapbox.backity.core.game.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.game.adapters.driving.api.http.model.GameWithFilesHttpDto;
import dev.codesoapbox.backity.core.game.adapters.driving.api.http.model.GameWithFilesHttpDtoMapper;
import dev.codesoapbox.backity.core.game.application.GameFacade;
import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final GameWithFilesHttpDtoMapper mapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageHttpDtoMapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @GetMapping
    public PageHttpDto<GameWithFilesHttpDto> getGames(
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        Page<GameWithFiles> gamesWithFiles = gameFacade.getGamesWithFiles(pagination);
        return pageHttpDtoMapper.toDto(gamesWithFiles, mapper::toDto);
    }
}
