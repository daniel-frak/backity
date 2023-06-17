package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.FileDiscoveryStatusHttpDto;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.FileDiscoveryStatusHttpDtoMapper;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsHttpDto;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsHttpDtoMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryService;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "File discovery", description = "Everything to do with discovering files available for download")
@RestController
@RequestMapping("discovered-files")
@RequiredArgsConstructor
public class FileDiscoveryController {

    private final FileDiscoveryService fileDiscoveryService;
    private final GameFileDetailsRepository repository;
    private final PageHttpDtoMapper pageMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final GameFileDetailsHttpDtoMapper gameFileDetailsMapper;

    @Operation(summary = "List discovered files",
            description = "Returns a paginated list of discovered files which were not yet added to the download queue")
    @GetMapping
    public PageHttpDto<GameFileDetailsHttpDto> getDiscoveredFiles(PaginationHttpDto pagination) {
        Page<GameFileDetails> gameFiles = repository.findAllDiscovered(paginationMapper.toModel(pagination));
        return pageMapper.toDto(gameFiles, gameFileDetailsMapper::toDto);
    }

    @Operation(summary = "Start file discovery", description = "Starts the process of file discovery")
    @GetMapping("discover")
    public void discover() {
        fileDiscoveryService.startFileDiscovery();
    }

    @Operation(summary = "Stop file discovery", description = "Stops the process of file discovery")
    @GetMapping("stop-discovery")
    public void stopDiscovery() {
        fileDiscoveryService.stopFileDiscovery();
    }

    @Operation(summary = "List discovery statuses",
            description = "Returns a list of discovery statuses for every remote client")
    @GetMapping("statuses")
    public List<FileDiscoveryStatusHttpDto> getStatuses() {
        return fileDiscoveryService.getStatuses().stream()
                .map(FileDiscoveryStatusHttpDtoMapper.INSTANCE::toDto)
                .toList();
    }
}
