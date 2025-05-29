package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.GetEnqueuedFileCopiesUseCase;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetProcessedFileCopiesUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.FileCopyProcessingStatusHttpDto;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FileCopiesRestResource
@RequiredArgsConstructor
@Slf4j
public class GetFileCopyListController {

    private final GetEnqueuedFileCopiesUseCase getEnqueuedFilesUseCase;
    private final GetProcessedFileCopiesUseCase getProcessedFilesUseCase;
    private final FileCopyHttpDtoMapper fileCopyMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List processed file copies with a given status",
            description = "Returns a paginated list of all file copies with a given status")
    @GetMapping
    public PageHttpDto<FileCopyHttpDto> getFileCopiesWithStatus(
            @RequestParam("processing-status") FileCopyProcessingStatusHttpDto status,
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);

        Page<FileCopy> foundPage = getFileCopyPage(status, pagination);

        return pageMapper.toDto(foundPage, fileCopyMapper::toDto);
    }

    private Page<FileCopy> getFileCopyPage(FileCopyProcessingStatusHttpDto status, Pagination pagination) {
        return switch (status) {
            case ENQUEUED -> getEnqueuedFilesUseCase.getEnqueuedFileCopies(pagination);
            case PROCESSED -> getProcessedFilesUseCase.getProcessedFileCopies(pagination);
        };
    }
}
