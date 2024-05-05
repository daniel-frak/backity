package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filedetails.application.GetProcessedFileListUseCase;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@FileDetailsRestResource
@RequiredArgsConstructor
@Slf4j
public class GetProcessedFileListController {

    private final GetProcessedFileListUseCase useCase;
    private final FileDetailsHttpDtoMapper fileDetailsMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List processed files",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @GetMapping("processed")
    public PageHttpDto<FileDetailsHttpDto> getProcessedFiles(PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        Page<FileDetails> foundPage = useCase.getProcessedFileList(pagination);
        return pageMapper.toDto(foundPage, fileDetailsMapper::toDto);
    }
}
