package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filedetails.application.GetDiscoveredFileListUseCase;
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
import org.springframework.web.bind.annotation.GetMapping;

@FileDetailsRestResource
@RequiredArgsConstructor
public class GetDiscoveredFilesController {

    private final GetDiscoveredFileListUseCase useCase;
    private final FileDetailsHttpDtoMapper fileDetailsMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List discovered files",
            description = "Returns a paginated list of discovered files which were not yet added to the download queue")
    @GetMapping("discovered")
    public PageHttpDto<FileDetailsHttpDto> getDiscoveredFiles(PaginationHttpDto paginationDto) {
        Pagination pagination = paginationMapper.toModel(paginationDto);
        Page<FileDetails> files = useCase.getDiscoveredFileList(pagination);
        return pageMapper.toDto(files, fileDetailsMapper::toDto);
    }
}
