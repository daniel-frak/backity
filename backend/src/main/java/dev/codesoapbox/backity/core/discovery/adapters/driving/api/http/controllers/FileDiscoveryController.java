package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model.FileDiscoveryStatusHttpDto;
import dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model.FileDiscoveryStatusHttpDtoMapper;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "File discovery", description = "Everything to do with discovering files available for download")
@RestController
@RequestMapping("file-discovery")
@RequiredArgsConstructor
public class FileDiscoveryController {

    private final FileDiscoveryService fileDiscoveryService;
    private final FileDiscoveryStatusHttpDtoMapper fileDiscoveryStatusMapper;

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
                .map(fileDiscoveryStatusMapper::toDto)
                .toList();
    }
}
