package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefiledetails.application.GetEnqueuedFileListUseCase;
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
public class GetEnqueuedFileListController {

    private final GetEnqueuedFileListUseCase useCase;
    private final GameFileDetailsHttpDtoMapper gameFileDetailsMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @GetMapping("queue")
    public PageHttpDto<GameFileDetailsHttpDto> getQueueItems(PaginationHttpDto paginationHttpDto) {
        Pagination pagination = paginationMapper.toModel(paginationHttpDto);
        Page<GameFileDetails> foundPage = useCase.getEnqueuedFileList(pagination);
        return pageMapper.toDto(foundPage, gameFileDetailsMapper::toDto);
    }
}
