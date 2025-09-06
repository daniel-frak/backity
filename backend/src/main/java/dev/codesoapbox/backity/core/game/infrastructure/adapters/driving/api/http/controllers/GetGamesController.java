package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesHttpDto;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.GameWithFileCopiesReadModelHttpDtoMapper;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@GameRestResource
@RequiredArgsConstructor
public class GetGamesController {

    private final GetGamesWithFilesUseCase getGamesWithFilesUseCase;
    private final GameWithFileCopiesReadModelHttpDtoMapper mapper;
    private final RequestPaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageHttpDtoMapper;

    @Operation(summary = "Get games", description = "Returns a paginated list of discovered games")
    @GetMapping
    public PageHttpDto<GameWithFileCopiesHttpDto> getGames(
            @Valid @Parameter(name = "pagination") RequestPaginationHttpDto paginationHttpDto,
            @RequestParam(name = "query", required = false) String searchQuery,
            @RequestParam(name = "file-copy-status", required = false) FileCopyStatusHttpDto fileCopyStatusDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        FileCopyStatus fileCopyStatus = Optional.ofNullable(fileCopyStatusDto)
                .map(dto -> FileCopyStatus.valueOf(dto.name()))
                .orElse(null);
        var filter = new GameWithFileCopiesSearchFilter(searchQuery, fileCopyStatus);
        Page<GameWithFileCopiesAndReplicationProgresses> gamesWithFiles =
                getGamesWithFilesUseCase.getGamesWithFiles(pagination, filter);

        return pageHttpDtoMapper.toDto(gamesWithFiles, mapper::toDto);
    }
}
