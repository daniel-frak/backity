package dev.codesoapbox.backity.core.game.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.game.adapters.driving.api.http.model.GameWithFilesHttpDto;
import dev.codesoapbox.backity.core.game.adapters.driving.api.http.model.GameWithFilesHttpDtoMapper;
import dev.codesoapbox.backity.core.game.application.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GameRestResource
@RequiredArgsConstructor
public class GetGamesController {

    private final GetGamesWithFilesUseCase getGamesWithFilesUseCase;
    private final GameWithFilesHttpDtoMapper mapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageHttpDtoMapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @GetMapping
    public PageHttpDto<GameWithFilesHttpDto> getGames(
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        Page<GameWithFiles> gamesWithFiles = getGamesWithFilesUseCase.getGamesWithFiles(pagination);
        return pageHttpDtoMapper.toDto(gamesWithFiles, mapper::toDto);
    }
}
