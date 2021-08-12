package dev.codesoapbox.backity.core.files.downloading.presentation;

import dev.codesoapbox.backity.core.files.downloading.application.services.FileDownloadFacade;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
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

    private final FileDownloadFacade fileDownloadFacade;

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping
    public Page<EnqueuedFileDownload> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return fileDownloadFacade.getQueueItems(pageable);
    }

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{discoveredFileUniqueId}")
    public void download(@PathVariable UUID discoveredFileUniqueId) {
        fileDownloadFacade.download(discoveredFileUniqueId);
    }
}
