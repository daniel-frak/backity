package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionBackupJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileVersionJsonMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
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

@Tag(name = "Backups", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("backups")
@RequiredArgsConstructor
@Slf4j
public class FileBackupController {

    private final GameFileVersionBackupRepository gameFileVersionBackupRepository;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @PageableAsQueryParam
    @GetMapping("current")
    public GameFileVersionBackupJson getCurrentlyProcessing() {
        return gameFileVersionBackupRepository.findCurrentlyDownloading()
                .map(GameFileVersionJsonMapper.INSTANCE::toJson)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping("queue")
    public Page<GameFileVersionBackupJson> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return gameFileVersionBackupRepository.findAllWaitingForDownload(pageable)
                .map(GameFileVersionJsonMapper.INSTANCE::toJson);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @PageableAsQueryParam
    @GetMapping("processed")
    public Page<GameFileVersionBackupJson> getProcessedFiles(@Parameter(hidden = true) Pageable pageable) {
        return gameFileVersionBackupRepository.findAllProcessed(pageable)
                .map(GameFileVersionJsonMapper.INSTANCE::toJson);
    }

    // @TODO Should be POST
    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{gameFileVersionId}")
    public ResponseEntity<Void> download(@PathVariable Long gameFileVersionId) {
        Optional<GameFileVersionBackup> gameFileVersionBackup = gameFileVersionBackupRepository
                .findById(gameFileVersionId);

        if (gameFileVersionBackup.isPresent()) {
            gameFileVersionBackup.get().enqueue();
            gameFileVersionBackupRepository.save(gameFileVersionBackup.get());
            return ResponseEntity.ok().build();
        }

        log.warn("Could not enqueue file. Game file version not found: " + gameFileVersionId);
        return ResponseEntity.badRequest().build();
    }
}
