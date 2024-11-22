package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers.model.GameFileProcessingStatusHttpDto;
import dev.codesoapbox.backity.core.gamefile.application.GetDiscoveredFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.application.GetEnqueuedFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.application.GetProcessedFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GameFileRestResource
@RequiredArgsConstructor
@Slf4j
public class GetGameFileListController {

    private final GetDiscoveredFileListUseCase getDiscoveredFilesUseCase;
    private final GetEnqueuedFileListUseCase getEnqueuedFilesUseCase;
    private final GetProcessedFileListUseCase getProcessedFilesUseCase;
    private final GameFileHttpDtoMapper gameFileMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List processed files",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @GetMapping
    public PageHttpDto<GameFileHttpDto> getGameFiles(
            @RequestParam("processing-status") GameFileProcessingStatusHttpDto status,
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);

        Page<GameFile> foundPage = getGameFilePage(status, pagination);

        return pageMapper.toDto(foundPage, gameFileMapper::toDto);
    }

    private Page<GameFile> getGameFilePage(GameFileProcessingStatusHttpDto status, Pagination pagination) {
        return switch (status) {
            case DISCOVERED -> getDiscoveredFilesUseCase.getDiscoveredFileList(pagination);
            case ENQUEUED -> getEnqueuedFilesUseCase.getEnqueuedFileList(pagination);
            case PROCESSED -> getProcessedFilesUseCase.getProcessedFileList(pagination);
        };
    }
}
