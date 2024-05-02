package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefiledetails.application.GetProcessedFileListUseCase;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@GameFileDetailsRestResource
@RequiredArgsConstructor
@Slf4j
public class GetProcessedFileListController {

    private final GetProcessedFileListUseCase useCase;
    private final GameFileDetailsHttpDtoMapper gameFileDetailsMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List processed files",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @GetMapping("processed")
    public PageHttpDto<GameFileDetailsHttpDto> getProcessedFiles(PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        Page<GameFileDetails> foundPage = useCase.getProcessedFileList(pagination);
        return pageMapper.toDto(foundPage, gameFileDetailsMapper::toDto);
    }
}
