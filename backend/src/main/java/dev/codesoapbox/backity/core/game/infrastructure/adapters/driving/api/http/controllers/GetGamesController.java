package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesHttpDto;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesReadModelHttpDtoMapper;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GameRestResource
@RequiredArgsConstructor
public class GetGamesController {

    private final GetGamesWithFilesUseCase getGamesWithFilesUseCase;
    private final GameWithFileCopiesReadModelHttpDtoMapper mapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageHttpDtoMapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @GetMapping
    public PageHttpDto<GameWithFileCopiesHttpDto> getGames(
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto,
            @RequestParam(name = "query", required = false) String searchQuery) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        var filter = new GameWithFileCopiesSearchFilter(searchQuery);
        Page<GameWithFileCopiesAndReplicationProgresses> gamesWithFiles =
                getGamesWithFilesUseCase.getGamesWithFiles(pagination, filter);
        return pageHttpDtoMapper.toDto(gamesWithFiles, mapper::toDto);
    }
}
