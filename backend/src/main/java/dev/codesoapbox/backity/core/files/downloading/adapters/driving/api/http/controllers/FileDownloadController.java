package dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.discovery.domain.services.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileDownloadQueue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Downloads", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("downloads")
@RequiredArgsConstructor
public class FileDownloadController {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final FileDownloadQueue fileDownloadQueue;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @PageableAsQueryParam
    @GetMapping("current")
    public EnqueuedFileDownload getCurrentlyDownloading() {
        return fileDownloadQueue.findCurrentlyDownloading()
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping("queue")
    public Page<EnqueuedFileDownload> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return fileDownloadQueue.findAllQueued(pageable);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @PageableAsQueryParam
    @GetMapping("processed")
    public Page<EnqueuedFileDownload> getProcessedFiles(@Parameter(hidden = true) Pageable pageable) {
        return fileDownloadQueue.findAllProcessed(pageable);
    }

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{discoveredFileUniqueId}")
    public void download(@PathVariable UUID discoveredFileUniqueId) {
        discoveredFileRepository.findByUniqueId(discoveredFileUniqueId)
                .ifPresentOrElse(fileDownloadQueue::enqueue, () -> {
                    throw new IllegalArgumentException("Discovered file not found: " + discoveredFileUniqueId);
                });
    }
}
