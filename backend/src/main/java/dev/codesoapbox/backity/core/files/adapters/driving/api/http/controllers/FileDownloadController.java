package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionJsonMapper;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.downloading.services.FileDownloadQueue;
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

@Tag(name = "Downloads", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("downloads")
@RequiredArgsConstructor
@Slf4j
public class FileDownloadController {

    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileDownloadQueue fileDownloadQueue;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @PageableAsQueryParam
    @GetMapping("current")
    public GameFileVersionJson getCurrentlyDownloading() {
        return gameFileVersionRepository.findCurrentlyDownloading()
                .map(GameFileVersionJsonMapper.INSTANCE::toJson)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping("queue")
    public Page<GameFileVersionJson> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return gameFileVersionRepository.findAllWaitingForDownload(pageable)
                .map(GameFileVersionJsonMapper.INSTANCE::toJson);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @PageableAsQueryParam
    @GetMapping("processed")
    public Page<GameFileVersionJson> getProcessedFiles(@Parameter(hidden = true) Pageable pageable) {
        return gameFileVersionRepository.findAllProcessed(pageable)
                .map(GameFileVersionJsonMapper.INSTANCE::toJson);
    }

    // @TODO Should be POST
    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{gameFileVersionId}")
    public ResponseEntity<Void> download(@PathVariable Long gameFileVersionId) {
        Optional<GameFileVersion> gameFileVersion = gameFileVersionRepository.findById(gameFileVersionId);

        if (gameFileVersion.isPresent()) {
            fileDownloadQueue.enqueue(gameFileVersion.get());
            return ResponseEntity.ok().build();
        }

        log.warn("Could not enqueue file. Game file version not found: " + gameFileVersionId);
        return ResponseEntity.badRequest().build();
    }
}
