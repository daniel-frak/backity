package dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.model.EnqueuedFileDownloadJson;
import dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.model.EnqueuedFileDownloadJsonMapper;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileDownloadQueue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Downloads", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("downloads")
@RequiredArgsConstructor
@Slf4j
public class FileDownloadController {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final FileDownloadQueue fileDownloadQueue;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @PageableAsQueryParam
    @GetMapping("current")
    public EnqueuedFileDownloadJson getCurrentlyDownloading() {
        return fileDownloadQueue.findCurrentlyDownloading()
                .map(EnqueuedFileDownloadJsonMapper.INSTANCE::toJson)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping("queue")
    public Page<EnqueuedFileDownloadJson> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return fileDownloadQueue.findAllQueued(pageable)
                .map(EnqueuedFileDownloadJsonMapper.INSTANCE::toJson);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @PageableAsQueryParam
    @GetMapping("processed")
    public Page<EnqueuedFileDownloadJson> getProcessedFiles(@Parameter(hidden = true) Pageable pageable) {
        return fileDownloadQueue.findAllProcessed(pageable)
                .map(EnqueuedFileDownloadJsonMapper.INSTANCE::toJson);
    }

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{discoveredFileUniqueId}")
    public ResponseEntity<Void> download(@PathVariable UUID discoveredFileUniqueId) {
        Optional<DiscoveredFile> discoveredFile = discoveredFileRepository.findByUniqueId(discoveredFileUniqueId);

        if (discoveredFile.isPresent()) {
            fileDownloadQueue.enqueue(discoveredFile.get());
            return ResponseEntity.ok().build();
        }

        log.warn("Could not enqueue file. Discovered file not found: " + discoveredFileUniqueId);
        return ResponseEntity.badRequest().build();
    }
}
