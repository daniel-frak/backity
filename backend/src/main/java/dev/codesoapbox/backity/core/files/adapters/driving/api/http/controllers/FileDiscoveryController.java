package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.FileDiscoveryStatusJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.FileDiscoveryStatusJsonMapper;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionBackupJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionJsonMapper;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final GameFileVersionBackupRepository repository;

    @Operation(summary = "List discovered files",
            description = "Returns a paginated list of discovered files which were not yet added to the download queue")
    @PageableAsQueryParam
    @GetMapping
    public Page<GameFileVersionBackupJson> getDiscoveredFiles(@Parameter(hidden = true) Pageable pageable) {
        return repository.findAllDiscovered(pageable)
                .map(GameFileVersionJsonMapper.INSTANCE::toJson);
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
    public List<FileDiscoveryStatusJson> getStatuses() {
        return fileDiscoveryService.getStatuses().stream()
                .map(FileDiscoveryStatusJsonMapper.INSTANCE::toJson)
                .toList();
    }
}
