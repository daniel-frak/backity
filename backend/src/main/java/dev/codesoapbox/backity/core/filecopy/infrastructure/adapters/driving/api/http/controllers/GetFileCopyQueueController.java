package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetFileCopyQueueUseCase;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDto;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDtoMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@FileCopyQueueRestResource
@RequiredArgsConstructor
@Slf4j
public class GetFileCopyQueueController {

    private final GetFileCopyQueueUseCase getFileCopyQueueUseCase;
    private final FileCopyWithContextHttpDtoMapper fileCopyWithContextMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "Get the file copy queue",
            description = "Returns a paginated list of file copies that are either enqueued or in progress")
    @GetMapping
    public PageHttpDto<FileCopyWithContextHttpDto> getFileCopyQueue(
            @Valid @Parameter(name = "pagination") PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);

        Page<FileCopyWithContext> foundPage = getFileCopyQueueUseCase.getFileCopyQueue(pagination);

        return pageMapper.toDto(foundPage, fileCopyWithContextMapper::toDto);
    }
}
