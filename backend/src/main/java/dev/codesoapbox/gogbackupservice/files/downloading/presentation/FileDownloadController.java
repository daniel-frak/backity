package dev.codesoapbox.gogbackupservice.files.downloading.presentation;

import dev.codesoapbox.gogbackupservice.files.downloading.application.services.FileDownloadFacade;
import dev.codesoapbox.gogbackupservice.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.gogbackupservice.shared.presentation.ApiControllerPaths;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Downloads")
@RestController
@RequestMapping(ApiControllerPaths.API + "downloads")
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileDownloadFacade fileDownloadFacade;

    @GetMapping
    public Page<EnqueuedFileDownload> getQueueItems(Pageable pageable) {
        return fileDownloadFacade.getQueueItems(pageable);
    }

    @GetMapping("enqueue/{discoveredFileUniqueId}")
    public void download(@PathVariable UUID discoveredFileUniqueId) {
        fileDownloadFacade.download(discoveredFileUniqueId);
    }
}
